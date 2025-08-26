package com.refit.app.ui.composable.combiking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import com.refit.app.R
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.local.wish.WishStore
import com.refit.app.data.combination.modelAndView.LikedCombinationViewModel
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.launch

@Composable
fun CombiKingSection(
    navController: NavController,
    category: com.refit.app.ui.composable.community.CommunityCategory,
    vm: LikedCombinationViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val wishStore = remember { WishStore(context) }
    val wishedIds by wishStore.wishedIds.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    // 정렬 옵션
    var expanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("인기순") }
    val sortOptions = listOf("인기순", "최신순", "가격낮은순", "가격높은순")

    val totalCount = if (state.combinations.isNotEmpty()) state.combinations.size else dummyCombinations.size

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // 상단 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 좌측 "총 n개의 조합"
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("총 ", fontSize = 14.sp, fontFamily = Pretendard)
                    Text(
                        "$totalCount",
                        color = MainPurple,
                        fontSize = 14.sp,
                        fontFamily = Pretendard
                    )
                    Text("개의 조합", fontSize = 14.sp, fontFamily = Pretendard)
                }

                // 우측 정렬 드롭다운
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { expanded = true }
                    ) {
                        Text(
                            text = selectedSort,
                            fontSize = 14.sp,
                            fontFamily = Pretendard,
                            color = Color.Black
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "정렬",
                            tint = Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedSort = option
                                    expanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            // 리스트
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyCombinations) { combination ->
                    CombinationCard(
                        combination = combination,
                        isSaved = wishedIds.contains(combination.combinationId),
                        onToggleSave = { id -> scope.launch { wishStore.toggle(id) } }
                    )
                }
            }
        }

        // 플로팅 버튼
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp)
                .graphicsLayer {
                    shadowElevation = 12.dp.toPx()
                    shape = CircleShape
                    clip = false
                    ambientShadowColor = Color.Black.copy(alpha = 0.2f)
                    spotShadowColor = Color.Black.copy(alpha = 0.2f)
                }
                .clickable { /* TODO 클릭 이벤트 */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_create_button),
                contentDescription = "생성 버튼",
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}
