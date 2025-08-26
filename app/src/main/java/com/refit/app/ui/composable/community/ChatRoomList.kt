package com.refit.app.ui.composable.community

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refit.app.data.chat.model.ChatRoom

@Composable
fun ChatRoomList(
    rooms: List<ChatRoom>,
    onClick: (ChatRoom) -> Unit
) {
    if (rooms.isEmpty()) {
        Text("대화방이 없습니다.", modifier = Modifier.padding(16.dp))
        return
    }

    LazyColumn {
        items(rooms, key = { it.categoryId }) { room ->
            ChatRoomItem(room = room, onClick = onClick)
            Divider()
        }
    }
}