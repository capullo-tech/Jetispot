package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import bruhcollective.itaysonlab.jetispot.ui.bottomsheets.fullscreen_player.BottomDrawer
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MoreOptionsDialog(
    useDialog: Boolean = false,
    dialogState: Boolean = false,
    drawerState: ModalBottomSheetState,
    onDismiss: () -> Unit,
    artworkUrl: String,
    title: String,
    artist: String,
) {
    val sheetContent: @Composable () -> Unit = {
        Column(modifier = Modifier) {
            Column(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center
            ) {
                PreviewableAsyncImage(
                    imageUrl = artworkUrl,
                    placeholderType = "track",
                    modifier = Modifier
                )
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = artist, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    if(!useDialog){
        BottomDrawer(drawerState = drawerState, sheetContent = {
            sheetContent()
        })
    } else if (dialogState){
        AlertDialog(onDismissRequest = onDismiss, text = {}, confirmButton = {}, dismissButton = {DismissButton(onClick = onDismiss)})
    }
}

@Composable
fun DismissButton(text: String = stringResource(R.string.dismiss), onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text)
    }
}