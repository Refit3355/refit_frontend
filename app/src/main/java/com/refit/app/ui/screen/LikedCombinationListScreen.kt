package com.refit.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.refit.app.data.local.combination.MyCombinationStore
import com.refit.app.data.combination.modelAndView.LikedCombinationViewModel
import com.refit.app.ui.composable.combination.CombinationCard
import kotlinx.coroutines.launch

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

    // MyCombinationStore에 저장된 combinationId 기반으로 조회
    LaunchedEffect(savedIds) {
        if (savedIds.isNotEmpty()) {
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

        else -> {
            LazyColumn {
                items(state.combinations) { combination ->
                    CombinationCard(
                        combination = combination,
                        isSaved = savedIds.contains(combination.combinationId),
                        onToggleSave = { id ->
                            scope.launch {
                                val wasSaved = savedIds.contains(id)

                                // SharedPreference에서 제거
                                myCombinationStore.toggle(id)

                                // 서버에도 반영
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
