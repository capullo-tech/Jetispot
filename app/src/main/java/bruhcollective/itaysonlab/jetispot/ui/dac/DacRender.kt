package bruhcollective.itaysonlab.jetispot.ui.dac

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.BuildConfig
import bruhcollective.itaysonlab.jetispot.proto.ErrorComponent
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.MediumActionCardBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.RecentlyPlayedSectionComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.RecsplanationHeadingComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.RecsplanationHeadingSingleTextComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.SectionComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.SectionHeaderComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.ShortcutsBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.SmallActionCardBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.ToolbarComponent2Binder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.ToolbarComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.BenefitListComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.DisclaimerComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.FallbackPlanComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.MultiUserMemberComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.PlanComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.SingleUserComponentBinder
import com.google.protobuf.Message
import com.spotify.allplans.v1.DisclaimerComponent
import com.spotify.allplans.v1.PlanComponent
import com.spotify.home.dac.component.heading.v1.proto.RecsplanationHeadingSingleTextComponent
import com.spotify.home.dac.component.v1.proto.AlbumCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.AlbumCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.ArtistCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardActionsMediumComponent
import com.spotify.home.dac.component.v1.proto.PlaylistCardActionsSmallComponent
import com.spotify.home.dac.component.v1.proto.RecentlyPlayedSectionComponent
import com.spotify.home.dac.component.v1.proto.RecsplanationHeadingComponent
import com.spotify.home.dac.component.v1.proto.SectionComponent
import com.spotify.home.dac.component.v1.proto.SectionHeaderComponent
import com.spotify.home.dac.component.v1.proto.ShortcutsSectionComponent
import com.spotify.home.dac.component.v1.proto.SnappyGridSectionComponent
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v2.proto.ToolbarComponentV2
import com.spotify.planoverview.v1.BenefitListComponent
import com.spotify.planoverview.v1.FallbackPlanComponent
import com.spotify.planoverview.v1.MultiUserMemberComponent
import com.spotify.planoverview.v1.SingleUserPrepaidComponent
import com.spotify.planoverview.v1.SingleUserRecurringComponent
import com.spotify.planoverview.v1.SingleUserTrialComponent

@Composable
fun DacRender(
  item: Message
) {
  when (item) {

    // AllPlans / PlanOverview
    is MultiUserMemberComponent -> MultiUserMemberComponentBinder(item)
    is BenefitListComponent -> BenefitListComponentBinder(item)
    is PlanComponent -> PlanComponentBinder(item)
    is DisclaimerComponent -> DisclaimerComponentBinder(item)
    is SingleUserRecurringComponent -> SingleUserComponentBinder(item)
    is SingleUserPrepaidComponent -> SingleUserComponentBinder(item)
    is SingleUserTrialComponent -> SingleUserComponentBinder(item)
    is FallbackPlanComponent -> FallbackPlanComponentBinder(item)

    // Home
    is ToolbarComponent -> ToolbarComponentBinder(item)
    is ToolbarComponentV2 -> ToolbarComponent2Binder(item)
    is ShortcutsSectionComponent -> ShortcutsBinder(item) // e.g. small card playlist, episode, etc. on home screen

    is AlbumCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "album", playCommand = item.playCommand)
    is ArtistCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.followUri, imageUri = item.imageUri, imagePlaceholder = "artist", playCommand = item.playCommand)
    is PlaylistCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "playlist", playCommand = item.playCommand)
    is AlbumCardActionsMediumComponent -> MediumActionCardBinder(title = item.title, subtitle = item.description, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "album", playCommand = item.playCommand, contentType = item.contentType, fact = item.conciseFact, gradientColor = item.gradientColor)
    is ArtistCardActionsMediumComponent -> MediumActionCardBinder(title = item.title, subtitle = item.description, navigateUri = item.navigateUri, likeUri = item.followUri, imageUri = item.imageUri, imagePlaceholder = "artist", playCommand = item.playCommand, contentType = item.contentType, fact = item.conciseFact, gradientColor = item.gradientColor)
    is PlaylistCardActionsMediumComponent -> MediumActionCardBinder(title = item.title, subtitle = item.description, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "playlist", playCommand = item.playCommand, contentType = item.contentType, fact = item.conciseFact, gradientColor = item.gradientColor)

    is RecsplanationHeadingComponent -> RecsplanationHeadingComponentBinder(item)
    is RecsplanationHeadingSingleTextComponent -> RecsplanationHeadingSingleTextComponentBinder(item)

    is SectionHeaderComponent -> SectionHeaderComponentBinder(item.title)
    is SectionComponent -> SectionComponentBinder(item)
    is RecentlyPlayedSectionComponent -> RecentlyPlayedSectionComponentBinder()

    //Podcasts
    //EpisodeCardActionsMediumComponent ->

    // is SnappyGridSectionComponent -> SnappyGridSectionComponentBinder(item)
    // Other

    is SnappyGridSectionComponent -> {}

    is ErrorComponent -> {
      if (BuildConfig.DEBUG) {
        Column {
          Text(
            if (item.type == ErrorComponent.ErrorType.UNSUPPORTED) {
              "DAC unsupported component"
            } else {
              "DAC rendering error"
            }, Modifier.padding(horizontal = 16.dp)
          )
          Text(
            item.message ?: "",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
              .padding(top = 4.dp)
              .padding(horizontal = 16.dp)
          )
        }
      }
    }

    else -> {
      if (BuildConfig.DEBUG) {
        Text("DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}", modifier = Modifier.padding(16.dp))
      }
    }
  }
}