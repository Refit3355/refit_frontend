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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.data.me.modelAndView.MyCombinationViewModel
import com.refit.app.ui.composable.combination.CombinationCard

@Composable
fun CreatedCombinationListScreen(vm: MyCombinationViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyCombinations()
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
                        isSaved = false,
                        onToggleSave = {},
                        showSaveButton = false
                    )
                }
            }
        }
    }
}
