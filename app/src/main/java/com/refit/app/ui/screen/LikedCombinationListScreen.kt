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
import com.refit.app.data.local.wish.WishStore
import com.refit.app.data.local.combination.modelAndView.LikedCombinationViewModel
import com.refit.app.ui.composable.combination.CombinationCard
import kotlinx.coroutines.launch

@Composable
fun CombinationListScreen(vm: LikedCombinationViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    val context = LocalContext.current
    val wishStore = remember { WishStore(context) }
    val wishedIds by wishStore.wishedIds.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    // SharedPreference에 저장된 combinationId 기반으로 조회
    LaunchedEffect(wishedIds) {
        if (wishedIds.isNotEmpty()) {
            vm.loadLikedCombinations(wishedIds)
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
                        isSaved = wishedIds.contains(combination.combinationId),
                        onToggleSave = { id ->
                            scope.launch { wishStore.toggle(id) }
                        }
                    )
                }
            }
        }
    }
}
