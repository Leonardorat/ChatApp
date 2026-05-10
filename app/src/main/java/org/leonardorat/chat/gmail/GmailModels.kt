package org.leonardorat.chat.gmail


data class GmailListResponse(
    val messages: List<GmailMessageRef> = emptyList(),
    val nextPageToken: String? = null
)

data class GmailMessageRef(
    val id: String,
    val threadId: String? = null
)

data class GmailMessage(
    val id: String,
    val threadId: String? = null,
    val snippet: String? = null,
    val payload: GmailPayload? = null
)

data class GmailPayload(
    val mimeType: String? = null,
    val headers: List<GmailHeader> = emptyList(),
    val body: GmailBody? = null,
    val parts: List<GmailPayload> = emptyList()
)

data class GmailHeader(
    val name: String,
    val value: String
)

data class GmailBody(
    val data: String? = null
)

data class GmailSendRequest(
    val raw: String
)
data class GmailProfile(
    val emailAddress: String,
    val messagesTotal: Int? = null,
    val threadsTotal: Int? = null,
    val historyId: String? = null
)