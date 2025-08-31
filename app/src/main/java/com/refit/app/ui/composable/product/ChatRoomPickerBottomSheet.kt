package com.refit.app.ui.composable.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.composable.common.ChatCategory
import com.refit.app.ui.composable.common.beautyRooms
import com.refit.app.ui.composable.common.healthRooms
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomPickerBottomSheet(
    onDismiss: () -> Unit,
    onSelect: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "채팅방으로 공유",
                fontFamily = Pretendard,
                fontWeight = FontWeight(500),
                fontSize = 20.sp
            )

            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            SectionTitle("뷰티")

            RoomGroup(
                rooms = beautyRooms,
                onClick = { id ->
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                        onSelect(id)
                    }
                }
            )

            Spacer(Modifier.height(20.dp))
            SectionTitle("헬스")

            RoomGroup(
                rooms = healthRooms,
                onClick = { id ->
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                        onSelect(id)
                    }
                }
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontFamily = Pretendard,
        fontWeight = FontWeight(500),
        color = MainPurple
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun RoomGroup(
    rooms: List<ChatCategory>,
    onClick: (Long) -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        shape = shape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            rooms.forEachIndexed { index, room ->
                RoomRow(
                    room = room,
                    onClick = { onClick(room.id) }
                )
                if (index != rooms.lastIndex) {
                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomRow(room: ChatCategory, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = room.title,
                fontFamily = Pretendard,
                fontWeight = FontWeight(500),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
