package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf

@Stable
interface YlDelegate {
    suspend fun getDisplayName(ownerUsername: String): String
}

val LocalYlDelegate = staticCompositionLocalOf<YlDelegate> { error("YlDelegate should be initialized") }