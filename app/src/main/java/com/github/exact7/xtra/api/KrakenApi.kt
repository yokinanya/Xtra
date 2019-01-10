package com.github.exact7.xtra.api

import com.github.exact7.xtra.model.chat.VideoMessagesResponse
import com.github.exact7.xtra.model.kraken.clip.ClipsResponse
import com.github.exact7.xtra.model.kraken.clip.Period
import com.github.exact7.xtra.model.kraken.game.GamesResponse
import com.github.exact7.xtra.model.kraken.stream.StreamType
import com.github.exact7.xtra.model.kraken.stream.StreamsResponse
import com.github.exact7.xtra.model.kraken.user.User
import com.github.exact7.xtra.model.kraken.user.UserEmotesResponse
import com.github.exact7.xtra.model.kraken.user.UsersResponse
import com.github.exact7.xtra.model.kraken.video.BroadcastType
import com.github.exact7.xtra.model.kraken.video.Sort
import com.github.exact7.xtra.model.kraken.video.VideosResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface KrakenApi {

    @GET("games/top")
    fun getTopGames(@Query("limit") limit: Int, @Query("offset") offset: Int): Single<GamesResponse>

    @GET("streams/")
    fun getStreams(@Query("game") game: String?, @Query("language") languages: String?, @Query("stream_type") streamType: StreamType?, @Query("limit") limit: Int, @Query("offset") offset: Int): Single<StreamsResponse>

    @GET("streams/followed")
    fun getFollowedStreams(@Header("Authorization") token: String, @Query("stream_type") streamType: StreamType?, @Query("limit") limit: Int, @Query("offset") offset: Int): Single<StreamsResponse>

    @GET("videos/{id}/comments")
    fun getVideoChatLog(@Path("id") videoId: String, @Query("content_offset_seconds") offsetSeconds: Double, @Query("limit") limit: Int): Single<VideoMessagesResponse>

    @GET("videos/{id}/comments")
    fun getVideoChatLogAfter(@Path("id") videoId: String, @Query("cursor") cursor: String, @Query("limit") limit: Int): Single<VideoMessagesResponse>

    @GET("clips/top")
    fun getClips(@Query("channel") channel: String?, @Query("game") gameName: String?, @Query("language") languages: String?, @Query("period") period: Period?, @Query("trending") trending: Boolean?, @Query("limit") limit: Int, @Query("cursor") cursor: String?): Single<ClipsResponse>

    @GET("clips/followed")
    fun getFollowedClips(@Header("Authorization") token: String, @Query("trending") trending: Boolean?, @Query("limit") limit: Int, @Query("cursor") cursor: String?): Single<ClipsResponse>

    @GET("videos/top")
    fun getTopVideos(@Query("game") game: String?, @Query("period") period: com.github.exact7.xtra.model.kraken.video.Period?, @Query("broadcast_type") broadcastType: BroadcastType?, @Query("language") language: String?, @Query("sort") sort: Sort?, @Query("limit") limit: Int, @Query("offset") offset: Int): Single<VideosResponse>

    @GET("videos/followed")
    fun getFollowedVideos(@Header("Authorization") token: String, @Query("broadcast_type") broadcastType: BroadcastType?, @Query("language") language: String?, @Query("sort") sort: Sort?, @Query("limit") limit: Int, @Query("offset") offset: Int): Single<VideosResponse>

    @GET("channels/{id}/videos")
    fun getChannelVideos(@Path("id") channelId: Any, @Query("broadcast_type") broadcastType: BroadcastType?, @Query("sort") sort: Sort?, @Query("limit") limit: Int, @Query("offset") offset: Int): Single<VideosResponse>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: Int): Single<User>

    @GET("users/{username}")
    fun getUserByLogin(@Path("username") login: String): Single<User>

    @GET("users")
    fun getUsers(@Query("username") logins: String): Single<UsersResponse>

    @GET("users/{id}/emotes")
    fun getUserEmotes(@Path("id") userId: Int): Single<UserEmotesResponse>
}