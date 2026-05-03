package org.leonardorat.chat.gmail


import org.leonardorat.chat.auth.AuthManager
import org.leonardorat.chat.mail.ChatMailBuilder
import org.leonardorat.chat.mail.ChatMailParser
import org.leonardorat.chat.mail.ChatMessage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GmailService(
    private val authManager: AuthManager
) {
    private val api: GmailApi

    private val parser = ChatMailParser()
    private val builder = ChatMailBuilder()

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://gmail.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GmailApi::class.java)
    }

    suspend fun loadChatMessages(): List<ChatMessage> {
        val token = authManager.freshAccessToken()
        val authHeader = "Bearer $token"

        val list = api.listMessages(
            authorization = authHeader,
            query = "subject:[ChatMailTest]",
            maxResults = 3
        )

        return list.messages.mapNotNull { ref ->
            val fullMessage = api.getMessage(
                authorization = authHeader,
                id = ref.id,
                format = "full"
            )

            parser.parse(fullMessage)
        }
    }

    suspend fun sendChatMessage(
        from: String,
        to: String,
        text: String
    ) {
        val token = authManager.freshAccessToken()
        val authHeader = "Bearer $token"

        val raw = builder.buildRawEmail(
            from = from,
            to = to,
            text = text
        )

        api.sendMessage(
            authorization = authHeader,
            body = GmailSendRequest(raw = raw)
        )
    }
}