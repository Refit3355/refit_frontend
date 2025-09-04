package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.R
import com.refit.app.data.local.combination.MyCombinationStore
import com.refit.app.data.combination.modelAndView.LikedCombinationViewModel
import com.refit.app.ui.composable.combination.CombinationCard
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun LikedCombinationListScreen(
    navController: NavController,
    vm: LikedCombinationViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    val context = LocalContext.current
    val myCombinationStore = remember { MyCombinationStore(context) }
    val savedIds by myCombinationStore.savedIds.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    LaunchedEffect(savedIds) {
        if (savedIds.isEmpty()) {
            vm.clearLikedCombinations()
        } else {
            vm.loadLikedCombinations(savedIds)
        }
    }

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("에러: ${state.error}")
            }
        }

        state.combinations.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jellbbo_default),
                        contentDescription = "저장한 조합 없음",
                        modifier = Modifier.size(120.dp)
                    )
                    Text(
                        text = "아직 저장한 조합이 없어요.",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        else -> {
            LazyColumn {
                items(state.combinations) { combination ->
                    CombinationCard(
                        combination = combination,
                        isSaved = savedIds.contains(combination.combinationId),
                        onToggleSave = { id ->
                            scope.launch {
                                val wasSaved = savedIds.contains(id)
                                myCombinationStore.toggle(id)
                                if (wasSaved) {
                                    vm.dislikeCombination(id)
                                } else {
                                    vm.likeCombination(id)
                                }
                            }
                        },
                        showSaveButton = true,
                        onClick = { id ->
                            navController.navigate("combinationDetail/$id")
                        }
                    )
                }
            }
        }
    }
}
