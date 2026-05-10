package org.leonardorat.chat.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.leonardorat.chat.mail.ChatMessage

@Composable
fun AppScreen(
    authorized: Boolean,
    status: String,
    fromEmail: String,
    toEmail: String,
    inputText: String,
    messages: List<ChatMessage>,
    onConnectClick: () -> Unit,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("ChatMail hypothesis build")
        Text("Status: $status")

        Spacer(modifier = Modifier.height(12.dp))

        if (!authorized) {
            Button(onClick = onConnectClick) {
                Text("Connect Gmail")
            }
            return@Column
        }

        OutlinedTextField(
            value = fromEmail,
            onValueChange = onFromChange,
            label = { Text("From Gmail address") }
        )

        OutlinedTextField(
            value = toEmail,
            onValueChange = onToChange,
            label = { Text("To email") }
        )

        Row {
            Button(onClick = onRefreshClick) {
                Text("Refresh")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { message ->
                Text("${message.from}: ${message.text}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            label = { Text("Message") }
        )

        Button(onClick = onSendClick) {
            Text("Send")
        }
    }
}