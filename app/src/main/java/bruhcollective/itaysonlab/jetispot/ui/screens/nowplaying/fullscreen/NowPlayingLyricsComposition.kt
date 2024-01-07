package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.ext.fadingEdge
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel

@Composable
fun NowPlayingLyricsComposition(
    modifier: Modifier,
    viewModel: NowPlayingViewModel,
    rvStateProgress: Float,
    selectedLyricIndex: Int,
    backgroundColor: Color
) {
    Box(modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val offset = Offset(
                x = lerp(viewModel.lyricsCardParams.first.x, 0f, rvStateProgress),
                y = lerp(viewModel.lyricsCardParams.first.y, 0f, rvStateProgress),
            )

            val size = Size(
                width = lerp(
                    viewModel.lyricsCardParams.second.width.toFloat(),
                    size.width,
                    rvStateProgress
                ),
                height = lerp(
                    viewModel.lyricsCardParams.second.height.toFloat(),
                    size.height,
                    rvStateProgress
                ),
            )

            val radius = androidx.compose.ui.unit.lerp(12.dp, 0.dp, rvStateProgress).toPx()

            drawRoundRect(
                color = backgroundColor.copy(alpha = 0.2f),
                topLeft = offset,
                size = size,
                cornerRadius = CornerRadius(radius, radius)
            )
        }

        if (rvStateProgress != 0f) {
            Box(
                Modifier
                    .alpha(rvStateProgress)
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 56.dp)
                    .fadingEdge(vertical = 24.dp)
                    .offset {
                        IntOffset(x = 0, y = (48.dp.toPx() * (1f - rvStateProgress)).toInt())
                    }) {

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(viewModel.spLyricsController.currentLyricsLines) { index, line ->
                        LyricLine(
                            text = line.words,
                            selected = index == selectedLyricIndex
                        ) {
                            Log.i("NowPlayingLyricsComposition", "onClick: $index")
                            viewModel.seekToLyricLineByIndex(index)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LyricLine(
    text: String,
    selected: Boolean,
    onClick: () -> Unit = {}
) {
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.6f,
        label = "Lyrics line text alpha"
    )

    val animatedFontSize by animateIntAsState(
        targetValue = if (selected) 32 else 20,
        label = "Lyrics line text font size",
        animationSpec = tween(500, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f))
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = animatedFontSize.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alpha(alpha)
        )
    }
}

private fun lerp(a: Float, b: Float, to: Float): Float {
    return a + to * (b - a)
}