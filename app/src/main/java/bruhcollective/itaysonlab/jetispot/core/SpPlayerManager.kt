package bruhcollective.itaysonlab.jetispot.core

import android.media.AudioManager
import android.os.Build
import android.os.Looper
import android.os.Process
import android.util.Log
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetispot.playback.service.refl.SpReflect
import bruhcollective.itaysonlab.jetispot.playback.sp.AndroidSinkOutput
import bruhcollective.itaysonlab.jetispot.playback.sp.LowToHighQualityPicker
import bruhcollective.itaysonlab.jetispot.playback.sp.RadioRepository
import bruhcollective.itaysonlab.jetispot.proto.AudioNormalization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.audio.decoders.AudioQuality
import xyz.gianlu.librespot.player.Player
import xyz.gianlu.librespot.player.PlayerConfiguration
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpPlayerManager @Inject constructor(
  private val repository: RadioRepository,
  private val sessionManager: SpSessionManager,
  private val configurationManager: SpConfigurationManager
) {
  @Volatile
  private var _player: Player? = null
  private var _playerReflect: SpReflect? = null
  private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  fun reflect(): SpReflect {
    _player ?: error("Player not yet created!")

    if (_playerReflect == null) {
      _playerReflect = SpReflect { _player!! }
    }

    return _playerReflect!!
  }

  fun isPlayerAvailable () = _player != null
  fun playerNullable() = _player

  private fun protoQualityToLibrespot(src: bruhcollective.itaysonlab.jetispot.proto.AudioQuality) = when (src) {
    bruhcollective.itaysonlab.jetispot.proto.AudioQuality.LOW -> AudioQuality.LOW
    bruhcollective.itaysonlab.jetispot.proto.AudioQuality.NORMAL -> AudioQuality.NORMAL
    bruhcollective.itaysonlab.jetispot.proto.AudioQuality.HIGH -> AudioQuality.HIGH
    bruhcollective.itaysonlab.jetispot.proto.AudioQuality.VERY_HIGH -> AudioQuality.VERY_HIGH
    bruhcollective.itaysonlab.jetispot.proto.AudioQuality.FLAC -> AudioQuality.FLAC
    else -> AudioQuality.VERY_HIGH
  }

  fun createPlayer() {
    if (_player != null) return
    val pipeFilepath = repository.getPipeFilepath()
    _player = verifyNotMainThread {
      Log.d("SNAPSERVER", "Setting up player")
      Player(PlayerConfiguration.Builder().apply {
        setOutput(PlayerConfiguration.AudioOutput.PIPE)
        setOutputPipe(pipeFilepath?.let { File(it) })
        //setOutput(PlayerConfiguration.AudioOutput.CUSTOM)
        //setOutputClass(AndroidSinkOutput::class.java.name)

        val config = configurationManager.syncPlayerConfig()
        setCrossfadeDuration(config.crossfade)
        setEnableNormalisation(config.normalization)
        setAutoplayEnabled(config.autoplay)
        setPreloadEnabled(config.preload)

        setPreferredQualityPicker(LowToHighQualityPicker {
          protoQualityToLibrespot(configurationManager.syncPlayerConfig().preferredQuality)
        })

        setPreferredQuality(protoQualityToLibrespot(config.preferredQuality))

        setNormalisationPregain(when (config.normalizationLevel) {
          AudioNormalization.QUIET -> -5f
          AudioNormalization.BALANCED -> 3f
          AudioNormalization.LOUD -> 6f
          else -> 3f
        })

        //setVolumeSteps(100)
        //setInitialVolume(100)
      }.build(), sessionManager.session)
    }

    Log.d("SNAPSERVER", "Starting snapserver")
    startSnapcast(
      pipeFilepath!!,
      repository.getCacheDirPath(),
      repository.getNativeLibDirPath(),
    )
  }

  fun player(): Player {
    return _player ?: error("Player not yet created!")
  }

  fun release () = verifyNotMainThread {
    _player?.close()
    _player = null
    _playerReflect = null
  }

  //

  private fun <T> verifyNotMainThread (block: () -> T): T {
    if (Looper.getMainLooper() == Looper.myLooper()) throw IllegalStateException("This should be run only on the non-UI thread!")
    return block()
  }


  private fun startSnapcast(
    filifoFilepath: String,
    cacheDir: String,
    nativeLibraryDir: String,
  ) {

    serverScope.launch {
      Log.d("SNAPSERVER", "Starting snapserver on thread: ${Thread.currentThread().name}")
      val snapserver = async {
        snapcastProcess(
          filifoFilepath,
          cacheDir,
          nativeLibraryDir,
        )
      }
      awaitAll( snapserver)
    }
  }

  private suspend fun snapcastProcess(
    filifoFilepath: String,
    cacheDir: String,
    nativeLibDir: String,
  ) = withContext(Dispatchers.IO) {
      val streamName = "name=RadioCapullo"
      val pipeMode = "mode=read"
      val dryoutMs = "dryout_ms=2000"
      val librespotSampleFormat = "sampleformat=44100:16:2"
      val pipeArgs = listOf(
        streamName, pipeMode, dryoutMs, librespotSampleFormat
      ).joinToString("&")

      val pb = ProcessBuilder()
        .command(
          "$nativeLibDir/libsnapserver.so",
          "--server.datadir=$cacheDir",
          "--stream.source",
          "pipe://$filifoFilepath?$pipeArgs",
        )
        .redirectErrorStream(true)
    try {
      val env = pb.environment()
      //if (rate != null) env["SAMPLE_RATE"] = rate
      //if (fpb != null) env["FRAMES_PER_BUFFER"] = fpb

      val process = pb.start()
      val bufferedReader = BufferedReader(
        InputStreamReader(process.inputStream)
      )
      var line: String?
      while (bufferedReader.readLine().also { line = it } != null) {
        val processId = Process.myPid()
        val threadName = Thread.currentThread().name
        val tag = "SNAPSERVER"
        Log.d(tag, "Running on: $processId -  $threadName - ${line!!}")
      }
    } catch (e: IOException) {
      Log.d("SNAPSERVER", "Error starting snapserver", e)
      throw RuntimeException(e)
    }
  }
}