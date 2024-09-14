package bruhcollective.itaysonlab.jetispot.ui.screens.auth

import android.content.Context
import android.content.res.Resources
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.HandlerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.playback.sp.AndroidZeroconfServer
import bruhcollective.itaysonlab.jetispot.playback.sp.RadioRepository
import bruhcollective.itaysonlab.jetispot.proto.AppConfig
import bruhcollective.itaysonlab.jetispot.proto.AudioQuality
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import com.spotify.connectstate.Connect
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import xyz.gianlu.librespot.core.Session
import java.io.File
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@Stable
@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val authManager: SpAuthManager,
    private val resources: Resources,
    private val repository: RadioRepository,
    private val spSessionManager: SpSessionManager,
    private val spPlayerManager: SpPlayerManager,
    private val spCollectionManager: SpCollectionManager,
    private val spConfigurationManager: SpConfigurationManager,
    @ApplicationContext private val applicationContext: Context,
) : ViewModel() {
    private val _isAuthInProgress = mutableStateOf(false)
    val isAuthInProgress: State<Boolean> = _isAuthInProgress
    val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun getDeviceName(): String = repository.getDeviceName()

    fun onLoginSuccess(navController: NavigationController) {
        navController.navigateAndClearStack(Screen.Feed)
        upgradeAudioQualityIfPremium()
    }

    fun auth(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        startNsdService { onSuccess() }
        /*
        if (isAuthInProgress.value) return

        viewModelScope.launch {
            if (username.isEmpty() || password.isEmpty()) {
                onFailure(resources.getString(R.string.auth_err_empty))
                return@launch
            }

            _isAuthInProgress.value = true

            when (val result = authManager.authWith(username, password)) {
                SpAuthManager.AuthResult.Success -> onSuccess()
                is SpAuthManager.AuthResult.Exception -> onFailure("Java Error: ${result.e.message}")
                is SpAuthManager.AuthResult.SpError -> onFailure(
                    when (result.msg) {
                        "BadCredentials" -> resources.getString(R.string.auth_err_badcreds)
                        "PremiumAccountRequired" -> resources.getString(R.string.auth_err_premium)
                        else -> "Spotify API error: ${result.msg}"
                    }
                )
            }

            _isAuthInProgress.value = false
        }
        */
    }

    fun startNsdService(
        onSuccess: () -> Unit
    ) {
        val pipeFilepath = repository.getPipeFilepath()
        if (pipeFilepath == null) {
            Log.e("CAPULLOWORKER", "Error creating FIFO file")
            return
        }
        val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
        val sessionListener = object : AndroidZeroconfServer.SessionListener {
            override fun sessionClosing(session: Session) {
                session.close()
            }

            override fun sessionChanged(session: Session) {
                Log.d("NSD", "Session changed on thread: ${Thread.currentThread().name}")
                // start the execution service from the main thread
                spSessionManager.setSession(session)
                spPlayerManager.createPlayer()
                spCollectionManager.init()
                mainThreadHandler.post { onSuccess() }
                /*
                executorService.execute(
                  SessionChangedRunnable(
                    session,
                    pipeFilepath,
                    object : SessionChangedCallback {
                      override fun onPlayerReady(player: Player) {
                        Log.d("NSD", "Player ready")
                      }

                      override fun onPlayerError(ex: Exception) {
                        Log.e("NSD", "Error creating player", ex)
                      }
                    }
                  )
                )
                */
            }
        }

        executorService.execute(
            ZeroconfServerRunnable(
                getDeviceName(),
                sessionListener,
                applicationContext
            )
        )

    }

    private class ZeroconfServerRunnable(
        val advertisingName: String,
        val sessionListener: AndroidZeroconfServer.SessionListener,
        val applicationContext: Context
    ) : Runnable {
        override fun run() {
            val server = prepareLibrespotSession(advertisingName)
            server.addSessionListener(sessionListener)

            val nsdManager = applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager

            val serviceInfo = NsdServiceInfo().apply {
                serviceName = "RadioCapullo"
                serviceType = "_spotify-connect._tcp"
                port = server.listenPort
                Log.d("NSD", "Service port: $port")
            }

            nsdManager.registerService(
                serviceInfo,
                NsdManager.PROTOCOL_DNS_SD,
                registrationListener
            )

            Runtime.getRuntime().addShutdownHook(
                Thread {
                    try {
                        server.closeSession()
                        server.close()
                    } catch (ex: Exception) {
                        Log.e("CAPULLO", "Error closing Zeroconf server", ex)
                    }
                }
            )
        }

        private val registrationListener = object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                Log.d("NSD", "Service registered")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Registration failed! Put debugging code here to determine why.
                Log.d("NSD", "Registration failed")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d("NSD", "Service unregistered")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Unregistration failed. Put debugging code here to determine why.
                Log.d("NSD", "Unregistration failed")
            }
        }
        private fun prepareLibrespotSession(advertisingName: String): AndroidZeroconfServer {
            // Configure the Spotify advertising session
            val conf = Session.Configuration.Builder()
                //.setStoreCredentials(false)
                //.setCacheEnabled(false)
                .setCacheEnabled(true).setDoCacheCleanUp(true)
                .setCacheDir(File(applicationContext.cacheDir, "spa_cache"))
                .setStoredCredentialsFile(File(applicationContext.filesDir, "spa_creds"))
                .build()
            val builder = AndroidZeroconfServer.Builder(applicationContext, conf)
                .setPreferredLocale(Locale.getDefault().language)
                .setDeviceType(Connect.DeviceType.SPEAKER)
                .setDeviceId(null)
                .setDeviceName(advertisingName)
            return builder.create()
        }
    }
    private suspend fun modifyDatastore(runOnBuilder: AppConfig.Builder.() -> Unit) {
        spConfigurationManager.dataStore.updateData {
            it.toBuilder().apply(runOnBuilder).build()
        }
    }

    private fun upgradeAudioQualityIfPremium() {
        viewModelScope.launch {
            if (spSessionManager.session.getUserAttribute("player-license") == "premium") {
                modifyDatastore {
                    playerConfig =
                        playerConfig.toBuilder().setPreferredQuality(AudioQuality.VERY_HIGH).build()
                }
            }
        }
    }
}
