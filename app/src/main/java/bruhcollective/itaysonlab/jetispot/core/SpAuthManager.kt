package bruhcollective.itaysonlab.jetispot.core

import android.util.Log
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpAuthManager @Inject constructor(
    private val spSessionManager: SpSessionManager,
    private val spPlayerManager: SpPlayerManager,
    private val spCollectionManager: SpCollectionManager
) {
    suspend fun authSuccess() = withContext(Dispatchers.IO) {
        spPlayerManager.createPlayer()
        spCollectionManager.init()
    }

    suspend fun authStored() = withContext(Dispatchers.IO) {
        runCatching {
            spSessionManager.setSession(spSessionManager.createSession().stored().create())
            spPlayerManager.createPlayer()
            spCollectionManager.init()
        }.onSuccess {
            Log.i("SpAuthManager", "auth stored success")
            AuthResult.Success
        }.onFailure { th ->
            Log.i("SpAuthManager", "auth stored failure", th)
            AuthResult.Exception(Exception(th))
        }
    }

    sealed class AuthResult {
        object Success : AuthResult()
        class Exception(val e: kotlin.Exception) : AuthResult()
    }

    fun reset() {
        Log.i("SpAuthManager", "resetting session")
        File(spSessionManager.appContext.filesDir, SpSessionManager.SP_CREDENTIALS_FILE).delete()
    }
}