package bruhcollective.itaysonlab.jetispot.ui.screens.history

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.test_feed.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.AbsHubViewModel
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.HubScaffold
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.ToolbarOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun ListeningHistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    HubScaffold(
        appBarTitle = stringResource(id = R.string.listening_history),
        state = viewModel.state,
        viewModel = viewModel,
        reloadFunc = { scope.launch { viewModel.reload() } },
        toolbarOptions = ToolbarOptions(big = true, alwaysVisible = true)
    )

    Text(text = viewModel.feedResponse?.body.toString())
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val api: SpApiManager,
    private val spInternalApi: SpInternalApi,
    private val spPlayerServiceManager: SpPlayerServiceManager
) : AbsHubViewModel(), HubScreenDelegate {

    //create an empty list of hubitems
    val hubItems = mutableListOf<HubItem>()
    var feedResponse: HubResponse = HubResponse("", null, hubItems, "")

    suspend fun load() = load {
        val feed_repsonse  = api.internal.getHomeView()
        feedResponse = feed_repsonse
      spInternalApi.getListeningHistory()
    }

    suspend fun reload() = reload {
      spInternalApi.getListeningHistory()
    }

    override fun play(data: PlayFromContextData) {
        spPlayerServiceManager.play(data.uri, data.player)
    }
}