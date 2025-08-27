package com.refit.app.ui.composable.analysis

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refit.app.ui.theme.MainPurple

enum class DetailTab { Detailed, Simple }

@Composable
fun DetailTabs(selected: DetailTab, onSelected: (DetailTab) -> Unit) {
    TabRow(
        selectedTabIndex = selected.ordinal,
        containerColor = Color.Transparent,
        contentColor = MainPurple,
        indicator = { tabs ->
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabs[selected.ordinal]),
                color = MainPurple, height = 3.dp
            )
        },
        divider = {}
    ) {
        Tab(selected == DetailTab.Detailed, { onSelected(DetailTab.Detailed) }, text = { Text("자세하게") })
        Tab(selected == DetailTab.Simple, { onSelected(DetailTab.Simple) }, text = { Text("단순하게") })
    }
}