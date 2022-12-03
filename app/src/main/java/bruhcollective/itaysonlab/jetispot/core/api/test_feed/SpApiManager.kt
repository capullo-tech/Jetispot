package bruhcollective.itaysonlab.jetispot.core.api.test_feed

import bruhcollective.itaysonlab.jetispot.core.api.test_feed.edges.SpInternalApi_FeedTest
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("BlockingMethodInNonBlockingContext", "OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalStdlibApi::class)
@Singleton
class SpApiManager @Inject constructor(
    val internal: SpInternalApi_FeedTest
)