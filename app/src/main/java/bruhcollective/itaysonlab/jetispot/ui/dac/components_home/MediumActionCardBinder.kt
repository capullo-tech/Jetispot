package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.monet.ColorToScheme
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks.DynamicLikeButton
import bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks.DynamicPlayButton
import bruhcollective.itaysonlab.jetispot.ui.shared.navClickable
import com.spotify.dac.player.v1.proto.PlayCommand

@Composable
fun MediumActionCardBinder(
    title: String,
    subtitle: String,
    contentType: String,
    fact: String,
    gradientColor: String,
    navigateUri: String,
    likeUri: String,
    imageUri: String,
    imagePlaceholder: String,
    playCommand: PlayCommand
) {
    val curScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    var colorScheme by remember { mutableStateOf(curScheme) }

    LaunchedEffect(gradientColor) {
        val clr = android.graphics.Color.parseColor("#$gradientColor")
        colorScheme = ColorToScheme.convert(clr, isDark)
    }

    MaterialTheme(colorScheme = colorScheme) {
        Card(colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(
                3.dp
            )
        ), modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .navClickable { navController ->
                navController.navigate(navigateUri)
            }) {
            Column(Modifier.padding(16.dp)) {
                Row {
                    PreviewableAsyncImage(
                        imageUrl = imageUri, placeholderType = imagePlaceholder, modifier = Modifier
                            .fillMaxHeight()
                            .size(140.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Subtext(text = contentType)
                        MediumText(text = title, maxLines = 2)
                        Spacer(modifier = Modifier.height(8.dp))
                        Subtext(text = subtitle)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    DynamicLikeButton(
                        objectUrl = likeUri,
                        Modifier.size(42.dp)
                    )

                    Spacer(Modifier.weight(1f))

                    Subtext(text = fact)

                    Spacer(modifier = Modifier.width(16.dp))

                    DynamicPlayButton(
                        command = playCommand,
                        Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}