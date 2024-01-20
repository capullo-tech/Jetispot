package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionEntry
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionEpisode
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionPinnedItem
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionShow
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.PredefCeType
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.core.user
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalFoundationApi::class
)
@Composable
fun YourLibraryContainerScreen(
  viewModel: YourLibraryContainerViewModel = hiltViewModel()
) {
  val navController = LocalNavigationController.current
  val focusManager = LocalFocusManager.current
  val scope = rememberCoroutineScope()
  val state = rememberLazyListState()

  var search by remember { mutableStateOf(false) }
  var query by remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    launch {
      viewModel.load()
    }
  }

  Scaffold(topBar = {
    Column {
      TopAppBar(
        title = {
          val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
          AnimatedContent(
            search,
            transitionSpec = {
              if (targetState) {
                (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
              } else {
                (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
              }.using(SizeTransform(clip = false))
            },
            label = "Your library search bar slide"
          ) {
            if (it) {
              BasicTextField(
                value = query,
                onValueChange = { q ->
                  query = q
                  viewModel.filter(q)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(
                  imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions {
                  focusManager.clearFocus()
                }
              ) { inner ->
                OutlinedTextFieldDefaults.DecorationBox(
                  value = query,
                  innerTextField = inner,
                  enabled = true,
                  singleLine = true,
                  visualTransformation = VisualTransformation.None,
                  interactionSource = remember { MutableInteractionSource() },
                  trailingIcon = {
                    if (query.isNotEmpty()) {
                      IconButton(onClick = { query = "" }) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                      }
                    }
                  },
                  placeholder = {
                    Text(stringResource(R.string.search_your_library))
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                  ),
                  contentPadding = OutlinedTextFieldDefaults.contentPadding()
                )
              }
            } else {
              Text(stringResource(id = R.string.your_library), fontWeight = FontWeight.SemiBold)
            }
          }
        },
        navigationIcon = {
          AnimatedContent(
            search,
            transitionSpec = {
              if (targetState) {
                (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
              } else {
                (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
              }.using(SizeTransform(clip = false))
            },
            label = "Your library nav icon slide"
          ) {
            if (it) {
              IconButton(onClick = { search = false }) {
                Icon(Icons.Rounded.ArrowBack, null)
              }
            } else {
              IconButton(
                onClick = { navController.navigate("spotify:config") },
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
              ) {
                PreviewableAsyncImage(imageUrl = viewModel.profilePicture, placeholderType = "user", modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape))
              }
            }
          }
        },
        actions = {
          if (!search) {
            IconButton(onClick = { search = true }) {
              Icon(Icons.Rounded.Search, null)
            }
          }
        }
      )

      AnimatedChipRow(
        listOf(
          ChipItem("playlists", stringResource(id = R.string.filter_playlist)),
          ChipItem("artists", stringResource(id = R.string.filter_artist)),
          ChipItem("albums", stringResource(id = R.string.filter_album)),
          ChipItem("shows", stringResource(id = R.string.filter_show))
        ),
        viewModel.selectedTabId
      ) {
        viewModel.selectedTabId = it
        scope.launch {
          viewModel.load()
          if (viewModel.selectedTabId == "") {
            delay(25L)
            state.animateScrollToItem(0)
          }
        }
      }
    }
  }, contentWindowInsets = WindowInsets(bottom = 0.dp)) { padding ->
    if (viewModel.loaded) {
      LazyColumn(
        state = state,
        modifier = Modifier
          .padding(padding)
          .fillMaxSize()
      ) {
        if (viewModel.filteredContent.isNotEmpty()) {
          items(
            viewModel.filteredContent,
            key = { it.javaClass.simpleName + "_" + it.ceId() },
            contentType = { it.javaClass.simpleName }) { item ->
            CompositionLocalProvider(LocalYlDelegate provides viewModel) {
              YlRenderer(item, modifier = Modifier
                .clickable { navController.navigate(item.ceUri()) }
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .animateItemPlacement())
            }
          }
        } else {
          item("NoSearchResult") {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .animateItemPlacement(),
              contentAlignment = Alignment.Center
            ) {
              Text(stringResource(R.string.search_no_results))
            }
          }
        }
      }
    } else {
      PagingLoadingPage(
        modifier = Modifier
          .padding(padding)
          .fillMaxSize()
      )
    }
  }
}

class ChipItem(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnimatedChipRow(
  chips: List<ChipItem>,
  selectedId: String,
  onClick: (String) -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(chips.let {
      if (selectedId != "") it.filter { i -> i.id == selectedId } else it
    }, key = { it.id }) { item ->
      val selected = selectedId == item.id
      FilterChip(selected = selected, onClick = {
        onClick(if (selected) "" else item.id)
      }, label = {
        Text(item.name)
      }, leadingIcon = {
        if (selected) Icon(Icons.Rounded.Check, null)
      }, modifier = Modifier.animateItemPlacement())
    }
  }
}

@OptIn(FlowPreview::class)
@HiltViewModel
class YourLibraryContainerViewModel @Inject constructor(
  private val dao: LocalCollectionDao,
  private val spMetadataRequester: SpMetadataRequester,
  private val spSessionManager: SpSessionManager
) : ViewModel(), YlDelegate {
  private var content by mutableStateOf<List<CollectionEntry>>(emptyList())
  private val debouncedSearch = MutableStateFlow("")
  var loaded by mutableStateOf(false)
  var selectedTabId: String by mutableStateOf("")
  var filteredContent by mutableStateOf<List<CollectionEntry>>(emptyList())
  var profilePicture by mutableStateOf("")

  init {
    viewModelScope.launch {
      debouncedSearch
        .debounce(200L)
        .collectLatest { search ->
          if (content.isNotEmpty()) {
            filteredContent = if (search.isEmpty()) {
              content
            } else {
              content.filter { ce ->
                when (ce) {
                  is CollectionRootlistItem -> ce.name.contains(search, ignoreCase = true)
                  is CollectionArtist -> ce.name.contains(search, ignoreCase = true)
                  is CollectionPinnedItem -> ce.name.contains(search, ignoreCase = true)
                  is CollectionEpisode -> ce.name.contains(search, ignoreCase = true) || ce.showName.contains(search, ignoreCase = true)
                  is CollectionAlbum -> ce.name.contains(search, ignoreCase = true) || ce.rawArtistsData.contains(search, ignoreCase = true)
                  is CollectionShow -> ce.name.contains(search, ignoreCase = true) || ce.publisher.contains(search, ignoreCase = true)
                  else -> ce.ceId().contains(search, ignoreCase = true)
                }
              }
            }
          }
        }
    }
  }

  override suspend fun getDisplayName(ownerUsername: String) = spMetadataRequester.request {
    user("spotify:user:$ownerUsername")
  }.userProfiles["spotify:user:$ownerUsername"]?.name?.value ?: ownerUsername

  override suspend fun getPinnedRootlistPicture(uri: String): String? {
    return dao.getRootlistImage(uri)
  }

  suspend fun load() {
    getProfilePicture()
    val type = when (selectedTabId) {
      "playlists" -> FetchType.Playlists
      "albums" -> FetchType.Albums
      "artists" -> FetchType.Artists
      "shows" -> FetchType.Shows
      else -> FetchType.All
    }

    val albums = dao.getAlbums()
    val artists = dao.getArtists()
    val playlists = dao.getRootlist()
    val shows = dao.getShows()

    val pins = dao.getPins().filter { p ->
      when (type) {
        FetchType.Playlists -> p.uri.contains("playlist")
        FetchType.Artists -> p.uri.contains("artist")
        FetchType.Albums -> p.uri.contains("album")
        FetchType.Shows -> p.uri.contains("show")
        FetchType.All -> true
      }
    }

    content = (when (type) {
      FetchType.Playlists -> playlists
      FetchType.Artists -> artists
      FetchType.Albums -> albums
      FetchType.Shows -> shows
      FetchType.All -> {
        (albums + artists + playlists + shows).sortedByDescending { it.ceTimestamp() }
      }
    }.toMutableList().also {
      it.addAll(0, pins)
      it.filter { p -> p.ceUri().startsWith("spotify:collection") }.forEach { pF ->
        when (pF.ceUri()) {
          "spotify:collection" -> pF.ceModifyPredef(PredefCeType.COLLECTION, dao.trackCount().toString())
          "spotify:collection:podcasts:episodes" -> pF.ceModifyPredef(PredefCeType.EPISODES, "")
          "spotify:collection:your-episodes" -> pF.ceModifyPredef(PredefCeType.YOUR_EPISODES, "")
        }
      }
      it.filter { p -> p.ceUri().startsWith("spotify:playlist:") }.map { pF -> pF.ceModifyPredef(PredefCeType.ROOTLIST, "") }
    }).distinctBy { it.ceUri() }
    filteredContent = content
    loaded = true
  }

  fun filter(search: String) {
    debouncedSearch.value = search
  }

  private suspend fun getProfilePicture() {
    val u = "spotify:user:${spSessionManager.session.username()}"
    profilePicture = spMetadataRequester.request {
      user(u)
    }.userProfiles[u]?.imagesList?.firstOrNull()?.url ?: ""
  }

  enum class FetchType {
    All,
    Playlists,
    Artists,
    Albums,
    Shows
  }
}