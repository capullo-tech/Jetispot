package bruhcollective.itaysonlab.jetispot.core

import android.content.Context
import androidx.core.content.edit
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import com.spotify.connectstate.Connect
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.core.Session
import java.io.File
import java.security.SecureRandom
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpSessionManager @Inject constructor(
    @ApplicationContext val appContext: Context,
) {
    private var _session: Session? = null
    val session get() = _session ?: throw IllegalStateException("Session is not created yet!")

    val spSessionDeviceName: String = SpUtils.getDeviceName(appContext)
    val spSessionDeviceType: Connect.DeviceType = Connect.DeviceType.SMARTPHONE
    val spSessionDeviceId: String = loadDeviceID()

    fun loadDeviceID(): String {
        val sharedPreferences = appContext.getSharedPreferences(
            SP_PREFERENCE_NAME,
            Context.MODE_PRIVATE,
        )
        var espotiDeviceId = sharedPreferences.getString(SP_SESSION_DEVICE_ID_PREFERENCE, null)

        if (espotiDeviceId == null) {
            // Generate a new device ID
            espotiDeviceId =
                Utils.randomHexString(
                    SecureRandom(),
                    SP_SESSION_DEVICE_ID_LENGTH,
                ).lowercase(Locale.getDefault())

            // Save it for future use
            sharedPreferences.edit { putString(SP_SESSION_DEVICE_ID_PREFERENCE, espotiDeviceId) }
        }

        return espotiDeviceId
    }

    fun createSession(): Session.Builder = Session.Builder(createCfg())
        .setDeviceType(spSessionDeviceType)
        .setDeviceName(spSessionDeviceName)
        .setDeviceId(spSessionDeviceId)
        .setPreferredLocale(Locale.getDefault().language)

    private fun createCfg() = Session.Configuration.Builder()
        .setCacheEnabled(true)
        .setDoCacheCleanUp(true)
        .setCacheDir(File(appContext.cacheDir, SP_CACHE_DIR ))
        .setStoredCredentialsFile(File(appContext.filesDir, SP_CREDENTIALS_FILE ))
        .build()

    fun isSignedIn() = _session?.isValid == true
    fun setSession(s: Session) {
        _session = s
    }

    companion object {
        private const val SP_SESSION_DEVICE_ID_PREFERENCE = "sp_session_device_id"
        private const val SP_PREFERENCE_NAME = "sp_preferences"
        private const val SP_CACHE_DIR = "spa_cache"
        const val SP_CREDENTIALS_FILE = "spa_creds"

        private const val SP_SESSION_DEVICE_ID_LENGTH = 40
    }
}