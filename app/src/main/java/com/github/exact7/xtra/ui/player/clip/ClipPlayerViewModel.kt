package com.github.exact7.xtra.ui.player.clip

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.exact7.xtra.model.kraken.clip.Clip
import com.github.exact7.xtra.repository.PlayerRepository
import com.github.exact7.xtra.ui.OnQualityChangeListener
import com.github.exact7.xtra.ui.player.PlayerHelper
import com.github.exact7.xtra.ui.player.PlayerViewModel
import com.github.exact7.xtra.util.C
import com.google.android.exoplayer2.source.ExtractorMediaSource
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class ClipPlayerViewModel @Inject constructor(
        context: Application,
        private val playerRepository: PlayerRepository) : PlayerViewModel(context), OnQualityChangeListener {

    private companion object {
        const val TAG = "ClipPlayerViewModel"
    }

    private val _clip = MutableLiveData<Clip>()
    val clip: LiveData<Clip>
        get() = _clip
    private val factory: ExtractorMediaSource.Factory = ExtractorMediaSource.Factory(dataSourceFactory)
    private var playbackProgress: Long = 0
    private val prefs = context.getSharedPreferences(C.USER_PREFS, Context.MODE_PRIVATE)
    private val helper = PlayerHelper()
    val qualities: Map<String, String>
        get() = helper.urls!!
    val loaded: LiveData<Boolean>
        get() = helper.loaded
    val selectedQualityIndex: Int
        get() = helper.selectedQualityIndex
//    val chatMessages: LiveData<MutableList<ChatMessage>>
//        get() = helper.chatMessages
//    val newMessage: LiveData<ChatMessage>
//        get() = helper.newMessage

    override fun changeQuality(index: Int) {
        playbackProgress = player.currentPosition
        val quality = helper.urls!!.values.elementAt(index)
        play(quality)
        prefs.edit { putString(TAG, quality) }
        helper.selectedQualityIndex = index
    }

    override fun onResume() {
        super.onResume()
        player.seekTo(playbackProgress)
    }

    override fun onPause() {
        super.onPause()
        playbackProgress = player.currentPosition
    }

    fun setClip(clip: Clip) {
        if (_clip.value != clip) {
            _clip.value = clip
            playerRepository.fetchClipQualities(clip.slug)
                    .subscribe({
                        helper.urls = it
                        play(it.values.first())
                        helper.selectedQualityIndex = 0
                        helper.loaded.value = true
//                    if (clip.vod != null) {
//                        playerRepository.fetchSubscriberBadges(clip.broadcaster.id)
//                                .subscribe({ response ->
//
//                                }, { t ->
//
//                                })
//                                .addTo(compositeDisposable)
//
//                    }
                    }, {

                    })
                    .addTo(compositeDisposable)
        }
    }

    private fun play(url: String) {
        mediaSource = factory.createMediaSource(url.toUri())
        play()
        player.seekTo(playbackProgress)
    }
}