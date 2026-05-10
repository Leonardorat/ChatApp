package org.leonardorat.chat.ui.rooms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.leonardorat.chat.data.ChatRoomEntity

@Composable
fun ChatListScreen(
    rooms: List<ChatRoomEntity>,
    status: String,
    onCreateRoomClick: () -> Unit,
    onRoomClick: (String) -> Unit,
    onRefreshClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Чаты")
        Text(status)

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Button(onClick = onCreateRoomClick) {
                Text("Создать чат")
            }

            Button(onClick = onRefreshClick) {
                Text("Обновить")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(rooms) { room ->
                Column(
                    modifier = Modifier
                        .clickable { onRoomClick(room.id) }
                        .padding(vertical = 8.dp)
                ) {
                    Text(room.displayName)
                    Text(room.peerEmail)
                    if (room.lastMessageText != null) {
                        Text("Последнее: ${room.lastMessageText}")
                    }
                }
            }
        }
    }
}