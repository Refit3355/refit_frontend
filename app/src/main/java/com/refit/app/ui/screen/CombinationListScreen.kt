package com.refit.app.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.ui.composable.combination.CombinationCard
import com.refit.app.data.local.combination.modelAndView.CombinationViewModel
import androidx.compose.ui.platform.LocalContext
import com.refit.app.data.local.wish.WishStore
import kotlinx.coroutines.launch

@Composable
fun CombinationListScreen() {
    val vm: CombinationViewModel = viewModel()
    val combinations by vm.combinations.collectAsState()

    val context = LocalContext.current
    val wishStore = remember { WishStore(context) }
    val wishedIds by wishStore.wishedIds.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.loadCombinations()
    }

    LazyColumn {
        items(combinations.filter { wishedIds.contains(it.combinationId) }) { combination ->
            CombinationCard(
                combination = combination,
                isSaved = true,
                onToggleSave = { id ->
                    scope.launch {
                        wishStore.toggle(id)
                    }
                }
            )
        }
    }
}
