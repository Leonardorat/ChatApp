package org.leonardorat.chat.gmail


import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GmailApi {

    @GET("gmail/v1/users/me/messages")
    suspend fun listMessages(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 3
    ): GmailListResponse

    @GET("gmail/v1/users/me/messages/{id}")
    suspend fun getMessage(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("format") format: String = "full"
    ): GmailMessage

    @GET("gmail/v1/users/me/profile")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): GmailProfile

    @POST("gmail/v1/users/me/messages/send")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Body body: GmailSendRequest
    ): GmailMessageRef
}