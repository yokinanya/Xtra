package com.github.andreyasadchy.xtra.util.chat

import com.github.andreyasadchy.xtra.util.TwitchApiHelper
import org.json.JSONObject

interface PubSubListener {
    fun onPlaybackMessage(message: JSONObject)
    fun onStreamInfo(message: JSONObject)
    fun onRewardMessage(message: JSONObject)
    fun onPointsEarned(message: JSONObject)
    fun onClaimAvailable()
    fun onMinuteWatched()
    fun onRaidUpdate(message: JSONObject, openStream: Boolean)
}

data class PlaybackMessage(
    val live: Boolean? = null,
    val serverTime: Long? = null,
    val viewers: Int? = null)

data class StreamInfo(
    val title: String? = null,
    val gameId: String? = null,
    val gameName: String? = null)

data class PointsEarned(
    val pointsGained: Int? = null,
    val timestamp: Long? = null,
    val fullMsg: String? = null)

data class Raid(
    val raidId: String? = null,
    val targetId: String? = null,
    val targetLogin: String? = null,
    val targetName: String? = null,
    val targetProfileImage: String? = null,
    val viewerCount: Int? = null,
    val openStream: Boolean) {

    val targetLogo: String?
        get() = TwitchApiHelper.getTemplateUrl(targetProfileImage, "profileimage")
}
