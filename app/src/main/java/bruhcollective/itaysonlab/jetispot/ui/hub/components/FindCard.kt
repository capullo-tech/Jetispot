package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

private val fallbackColors = listOf("#148a08", "#e1118c", "#27856a", "#283ea3", "#0d73ec", "#e8115b")

@Composable
fun FindCard(
    item: HubItem
) {
    Card(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .clickableHub(item),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor((item.custom?.get("backgroundColor") as? String) ?: fallbackColors.random()))
        )
    ) {
        Box(Modifier.fillMaxSize()) {
            PreviewableAsyncImage(
                imageUrl = item.images?.main?.uri,
                placeholderType = item.images?.background?.placeholder,
                modifier = Modifier
                    .size(84.dp)
                    .offset(x = 24.dp, y = 12.dp)
                    .rotate(20f)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.BottomEnd)
            )
            Text(
                item.text!!.title!!,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
        }
    }
}