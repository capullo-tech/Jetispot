package bruhcollective.itaysonlab.jetispot.ui.screens.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val spSessionManager: SpSessionManager,
    private val spConfigurationManager: SpConfigurationManager,
    private val espotiNsdManager: EspotiNsdManager,
) : ViewModel() {
    private val _isAuthInProgress = mutableStateOf(true)
    val isAuthInProgress: State<Boolean> = _isAuthInProgress

    init {
        _isAuthInProgress.value = true

        viewModelScope.launch(Dispatchers.IO) {
            espotiNsdManager.start()
        }
        viewModelScope.launch {
            spSessionManager.sessionState.collect {
                it?.let { sessionState ->
                    if (sessionState is SpSessionManager.SessionState.Created) {
                        authManager.authSuccess()
                        _isAuthInProgress.value = false
                    }
                }
            }
        }
    }

    fun onAuthSuccess(navController: NavigationController) {
        navController.navigateAndClearStack(Screen.Feed)
        upgradeAudioQualityIfPremium()
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
