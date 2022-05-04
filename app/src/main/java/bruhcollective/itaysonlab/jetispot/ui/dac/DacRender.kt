package bruhcollective.itaysonlab.jetispot.ui.dac

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.*
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.BenefitListComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.DisclaimerComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.MultiUserMemberComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.PlanComponentBinder
import com.google.protobuf.Message
import com.spotify.allplans.v1.DisclaimerComponent
import com.spotify.allplans.v1.PlanComponent
import com.spotify.home.dac.component.v1.proto.*
import com.spotify.planoverview.v1.BenefitListComponent
import com.spotify.planoverview.v1.MultiUserMemberComponent

@Composable
fun DacRender (
  navController: NavController,
  item: Message
) {
  when (item) {
    // AllPlans / PlanOverview
    is MultiUserMemberComponent -> MultiUserMemberComponentBinder(navController, item)
    is BenefitListComponent -> BenefitListComponentBinder(navController, item)
    is PlanComponent -> PlanComponentBinder(navController, item)
    is DisclaimerComponent -> DisclaimerComponentBinder(navController, item)
    // Home
    is ToolbarComponent -> ToolbarComponentBinder(navController, item)
    is ShortcutsSectionComponent -> ShortcutsBinder(navController, item)
    is AlbumCardActionsSmallComponent -> SmallActionCardBinder(navController = navController, title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "album", playCommand = item.playCommand)
    is ArtistCardActionsSmallComponent -> SmallActionCardBinder(navController = navController, title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.followUri, imageUri = item.imageUri, imagePlaceholder = "artist", playCommand = item.playCommand)
    is PlaylistCardActionsSmallComponent -> SmallActionCardBinder(navController = navController, title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "playlist", playCommand = item.playCommand)
    is RecsplanationHeadingComponent -> RecsplanationHeadingComponentBinder(navController, item)
    is SectionHeaderComponent -> SectionHeaderComponentBinder(item.title)
    is SectionComponent -> SectionComponentBinder(navController, item)
    is RecentlyPlayedSectionComponent -> RecentlyPlayedSectionComponentBinder(navController)
    // Other
    else -> {
      Text("DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}