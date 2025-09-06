package com.refit.app.ui.composable.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.composable.common.ChatCategory
import com.refit.app.ui.composable.common.allRooms
import com.refit.app.ui.composable.common.beautyRooms
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
        containerColor = Color.White,
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 6.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "그룹 채팅으로 공유",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(500),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                GroupLegend()
            }

            Spacer(Modifier.height(24.dp))

            // 한 그리드(뷰티/헬스 합쳐서 표시) - 2열 직사각형 타일
            RoomGrid2Col(
                rooms = allRooms,
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

private enum class CategoryGroup { BEAUTY, HEALTH }

// 뷰티/헬스 판별(beautyRooms 기준)
private val beautyIdSet: Set<Long> = beautyRooms.map { it.id }.toSet()
private fun groupOf(id: Long): CategoryGroup =
    if (beautyIdSet.contains(id)) CategoryGroup.BEAUTY else CategoryGroup.HEALTH

@Composable
private fun RoomGrid2Col(
    rooms: List<ChatCategory>,
    onClick: (Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(rooms, key = { it.id }) { room ->
            RoomTile(
                room = room,
                group = groupOf(room.id),
                onClick = { onClick(room.id) }
            )
        }
    }
}

@Composable
private fun RoomTile(
    room: ChatCategory,
    group: CategoryGroup,
    onClick: () -> Unit
) {
    val barColor = groupColor(group)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // 직사각형 타일
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측 포인트 바
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 12.dp
                        )
                    )
                    .background(barColor)
            )

            Spacer(Modifier.width(12.dp))

            // 타이틀
            Text(
                text = room.title,
                fontFamily = Pretendard,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun GroupLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        LegendItem(label = "뷰티", color = groupColor(CategoryGroup.BEAUTY))
        LegendItem(label = "헬스", color = groupColor(CategoryGroup.HEALTH))
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = Pretendard
        )
    }
}

@Composable
private fun groupColor(group: CategoryGroup): Color = when (group) {
    CategoryGroup.BEAUTY -> Color(0xFFF27D98).copy(alpha = 0.6f)
    CategoryGroup.HEALTH -> MainPurple.copy(alpha = 0.5f)
}
