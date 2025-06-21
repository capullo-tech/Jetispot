package bruhcollective.itaysonlab.jetispot.ui.screens.auth

import android.content.res.Resources
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.proto.AppConfig
import bruhcollective.itaysonlab.jetispot.proto.AudioQuality
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.capullo.lib_librespot_android.EspotiNsdManager
import javax.inject.Inject

@Stable
@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val authManager: SpAuthManager,
    private val resources: Resources,
    private val spSessionManager: SpSessionManager,
    private val spConfigurationManager: SpConfigurationManager,
    private val espotiNsdManager: EspotiNsdManager,
) : ViewModel() {
    private val _isAuthInProgress = mutableStateOf(false)
    val isAuthInProgress: State<Boolean> = _isAuthInProgress

    init {
        viewModelScope.launch(Dispatchers.IO) {
            espotiNsdManager.start()
        }
    }
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
