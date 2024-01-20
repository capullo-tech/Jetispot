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
import bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks.DynamicPlayButton
import bruhcollective.itaysonlab.jetispot.ui.shared.navClickable
import com.spotify.home.dac.component.v1.proto.PromoCardHomeComponent

@Composable
fun PromoCardBinder(
    item: PromoCardHomeComponent
) {
    val imagePlaceholder = item.navigateUri.split(":").getOrNull(1) ?: "playlist"

    val curScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    var colorScheme by remember { mutableStateOf(curScheme) }

    LaunchedEffect(item.gradientColor) {
        val clr = android.graphics.Color.parseColor("#${item.gradientColor}")
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
                navController.navigate(item.navigateUri)
            }) {
            Column(Modifier.padding(16.dp)) {
                Row {
                    PreviewableAsyncImage(
                        imageUrl = item.logoImageUri, placeholderType = imagePlaceholder, modifier = Modifier
                            .fillMaxHeight()
                            .size(140.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        MediumText(text = item.title, maxLines = 2)
                        Spacer(modifier = Modifier.height(8.dp))
                        Subtext(text = item.subtitle)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.weight(1f))
                    DynamicPlayButton(
                        command = item.playCommand,
                        Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}