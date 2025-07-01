package bruhcollective.itaysonlab.jetispot.core.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.protobuf.ByteString
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.ImageId

object SpUtils {
    const val SPOTIFY_APP_VERSION = "8.7.68.568"

    fun getDeviceName(appContext: Context): String {
        val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val deviceName = Settings.Global.getString(appContext.contentResolver, Settings.Global.DEVICE_NAME)
            if (deviceName == Build.MODEL) Build.MODEL else "$deviceName (${Build.MODEL})"
        } else {
            Build.MODEL
        }
        return "Jetispot - $deviceName"
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

    fun getImageUrl(bytes: ByteString?) = if (bytes != null) "https://i.scdn.co/image/${Utils.bytesToHex(bytes).lowercase()}" else null
}