package com.github.andreyasadchy.xtra.ui.saved.bookmarks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.andreyasadchy.xtra.model.offline.Bookmark
import com.github.andreyasadchy.xtra.model.offline.VodBookmarkIgnoredUser
import com.github.andreyasadchy.xtra.repository.ApiRepository
import com.github.andreyasadchy.xtra.repository.BookmarksRepository
import com.github.andreyasadchy.xtra.repository.PlayerRepository
import com.github.andreyasadchy.xtra.repository.VodBookmarkIgnoredUsersRepository
import com.github.andreyasadchy.xtra.util.DownloadUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject internal constructor(
    @ApplicationContext private val applicationContext: Context,
    private val repository: ApiRepository,
    private val bookmarksRepository: BookmarksRepository,
    playerRepository: PlayerRepository,
    private val vodBookmarkIgnoredUsersRepository: VodBookmarkIgnoredUsersRepository) : ViewModel() {

    val positions = playerRepository.loadVideoPositions()
    val ignoredUsers = vodBookmarkIgnoredUsersRepository.loadUsers()
    private var updatedUsers = false
    private var updatedVideos = false

    val flow = Pager(
        PagingConfig(pageSize = 30, prefetchDistance = 3, initialLoadSize = 30),
    ) {
        bookmarksRepository.loadBookmarksPagingSource()
    }.flow.cachedIn(viewModelScope)

    fun delete(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarksRepository.deleteBookmark(applicationContext, bookmark)
        }
    }

    fun vodIgnoreUser(userId: String) {
        viewModelScope.launch {
            if (vodBookmarkIgnoredUsersRepository.getUserById(userId) != null) {
                vodBookmarkIgnoredUsersRepository.deleteUser(VodBookmarkIgnoredUser(userId))
            } else {
                vodBookmarkIgnoredUsersRepository.saveUser(VodBookmarkIgnoredUser(userId))
            }
        }
    }

    fun updateUsers(helixClientId: String? = null, helixToken: String? = null, gqlHeaders: Map<String, String>) {
        if (!updatedUsers) {
            updatedUsers = true
            viewModelScope.launch {
                try {
                    val allIds = bookmarksRepository.loadBookmarks().mapNotNull { bookmark -> bookmark.userId.takeUnless { it == null || ignoredUsers.value?.contains(VodBookmarkIgnoredUser(it)) == true } }
                    if (allIds.isNotEmpty()) {
                        for (ids in allIds.chunked(100)) {
                            val users = repository.loadUserTypes(ids, helixClientId, helixToken, gqlHeaders)
                            if (users != null) {
                                for (user in users) {
                                    val bookmarks = user.channelId?.let { bookmarksRepository.getBookmarksByUserId(it) }
                                    if (bookmarks != null) {
                                        for (bookmark in bookmarks) {
                                            if (user.type != bookmark.userType || user.broadcasterType != bookmark.userBroadcasterType) {
                                                bookmarksRepository.updateBookmark(bookmark.apply {
                                                    userType = user.type
                                                    userBroadcasterType = user.broadcasterType
                                                })
                                            }
                                        }
                                    }
                                }
                            }
                        }}
                } catch (e: Exception) {

                }
            }
        }
    }

    fun updateVideo(helixClientId: String? = null, helixToken: String? = null, gqlHeaders: Map<String, String>, videoId: String? = null) {
        viewModelScope.launch {
            try {
                val video = videoId?.let { repository.loadVideo(it, helixClientId, helixToken, gqlHeaders) }
                val bookmark = videoId?.let { bookmarksRepository.getBookmarkByVideoId(it) }
                if (video != null && bookmark != null) {
                    val downloadedThumbnail = video.id.takeIf { !it.isNullOrBlank() }?.let {
                        DownloadUtils.savePng(applicationContext, video.thumbnail, "thumbnails", it)
                    }
                    bookmarksRepository.updateBookmark(Bookmark(
                        videoId = bookmark.videoId,
                        userId = video.channelId ?: bookmark.userId,
                        userLogin = video.channelLogin ?: bookmark.userLogin,
                        userName = video.channelName ?: bookmark.userName,
                        userType = bookmark.userType,
                        userBroadcasterType = bookmark.userBroadcasterType,
                        userLogo = bookmark.userLogo,
                        gameId = video.gameId ?: bookmark.gameId,
                        gameSlug = video.gameSlug ?: bookmark.gameSlug,
                        gameName = video.gameName ?: bookmark.gameName,
                        title = video.title ?: bookmark.title,
                        createdAt = video.uploadDate ?: bookmark.createdAt,
                        thumbnail = downloadedThumbnail,
                        type = video.type ?: bookmark.type,
                        duration = video.duration ?: bookmark.duration,
                        animatedPreviewURL = video.animatedPreviewURL ?: bookmark.animatedPreviewURL
                    ))
                }
            } catch (e: Exception) {

            }
        }
    }

    fun updateVideos(helixClientId: String? = null, helixToken: String? = null) {
        if (!updatedVideos) {
            updatedVideos = true
            viewModelScope.launch {
                try {
                    val allIds = bookmarksRepository.loadBookmarks().mapNotNull { it.videoId }
                    if (allIds.isNotEmpty()) {
                        for (ids in allIds.chunked(100)) {
                            val videos = repository.loadVideos(ids, helixClientId, helixToken)
                            if (videos != null) {
                                for (video in videos) {
                                    val bookmark = video.id?.let { bookmarksRepository.getBookmarkByVideoId(it) }
                                    if (bookmark != null && (bookmark.userId != video.channelId ||
                                                bookmark.userLogin != video.channelLogin ||
                                                bookmark.userName != video.channelName ||
                                                bookmark.title != video.title ||
                                                bookmark.createdAt != video.uploadDate ||
                                                bookmark.type != video.type ||
                                                bookmark.duration != video.duration)) {
                                        val downloadedThumbnail = DownloadUtils.savePng(applicationContext, video.thumbnail, "thumbnails", video.id)
                                        bookmarksRepository.updateBookmark(Bookmark(
                                            videoId = bookmark.videoId,
                                            userId = video.channelId ?: bookmark.userId,
                                            userLogin = video.channelLogin ?: bookmark.userLogin,
                                            userName = video.channelName ?: bookmark.userName,
                                            userType = bookmark.userType,
                                            userBroadcasterType = bookmark.userBroadcasterType,
                                            userLogo = bookmark.userLogo,
                                            gameId = bookmark.gameId,
                                            gameSlug = bookmark.gameSlug,
                                            gameName = bookmark.gameName,
                                            title = video.title ?: bookmark.title,
                                            createdAt = video.uploadDate ?: bookmark.createdAt,
                                            thumbnail = downloadedThumbnail,
                                            type = video.type ?: bookmark.type,
                                            duration = video.duration ?: bookmark.duration,
                                            animatedPreviewURL = video.animatedPreviewURL ?: bookmark.animatedPreviewURL
                                        ))
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {

                }
            }
        }
    }
}