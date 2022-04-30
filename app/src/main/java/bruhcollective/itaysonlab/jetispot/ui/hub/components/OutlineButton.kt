package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText

@Composable
fun OutlineButton(
    navController: NavController,
    delegate: HubScreenDelegate,
    item: HubItem
) {
    Box(Modifier.clickableHub(navController, delegate, item).padding(16.dp)) {
        Row(Modifier.align(Alignment.Center)) {
            MediumText(text = item.text?.title!!, modifier = Modifier.align(Alignment.CenterVertically))
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 2.dp).size(20.dp).align(Alignment.CenterVertically))
        }
    }
}