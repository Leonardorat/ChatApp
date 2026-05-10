package org.leonardorat.chat.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.leonardorat.chat.data.MessageDirection
import org.leonardorat.chat.data.MessageEntity

@Composable
fun ChatScreen(
    roomTitle: String,
    messages: List<MessageEntity>,
    status: String,
    onSend: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(roomTitle)
        Text(status)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { message ->
                val author = if (message.direction == MessageDirection.OUTGOING) {
                    "Вы"
                } else {
                    message.fromEmail
                }

                Text("$author: ${message.text} (${message.status})")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Сообщение") }
        )

        Button(
            onClick = {
                if (input.isNotBlank()) {
                    onSend(input)
                    input = ""
                }
            }
        ) {
            Text("Отправить")
        }
    }
}