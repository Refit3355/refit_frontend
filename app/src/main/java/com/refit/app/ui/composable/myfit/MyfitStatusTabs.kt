package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refit.app.data.myfit.viewmodel.MyfitTab
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.LightGreyText

@Composable
private fun TabText(label: String, active: Boolean, activeColor: Color, inactiveColor: Color) {
    Text(label, color = if (active) activeColor else inactiveColor)
}

@Composable
fun MyfitStatusTabs(selected: MyfitTab, onSelect: (MyfitTab) -> Unit) {
    val purple = MainPurple
    val grey = LightGreyText

    TabRow(
        selectedTabIndex = selected.ordinal,
        containerColor = Color.White,
        indicator = { positions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(positions[selected.ordinal])
                    .height(2.dp),
                color = purple
            )
        }
    ) {
        Tab(
            selected = selected == MyfitTab.USING,
            onClick = { onSelect(MyfitTab.USING) },
            text = { TabText("사용중", selected == MyfitTab.USING, purple, grey) }
        )
        Tab(
            selected = selected == MyfitTab.COMPLETED,
            onClick = { onSelect(MyfitTab.COMPLETED) },
            text = { TabText("사용완료", selected == MyfitTab.COMPLETED, purple, grey) }
        )
        Tab(
            selected = selected == MyfitTab.REGISTER,
            onClick = { onSelect(MyfitTab.REGISTER) },
            text = { TabText("사용등록", selected == MyfitTab.REGISTER, purple, grey) }
        )
    }
}
