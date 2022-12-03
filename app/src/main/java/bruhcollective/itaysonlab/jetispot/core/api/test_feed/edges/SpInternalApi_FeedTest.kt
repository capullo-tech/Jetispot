package bruhcollective.itaysonlab.jetispot.core.api.test_feed.edges

import bruhcollective.itaysonlab.jetispot.core.api.test_feed.SpApiExecutor
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpInternalApi_FeedTest @Inject constructor(
    private val api: SpApiExecutor
): SpEdgeScope by SpApiExecutor.Edge.Internal.scope(api) {

    suspend fun getHomeView() = getJson<HubResponse>(
        "/homeview/v1/home", mapOf("is_car_connected" to "false")
    )

}