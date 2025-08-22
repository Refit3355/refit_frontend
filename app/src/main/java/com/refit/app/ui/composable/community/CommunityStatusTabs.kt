package com.refit.app.ui.composable.community

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.LightGreyText
import com.refit.app.ui.theme.Pretendard

enum class CommunityTab(val param: String) { CHAT("chat"), COMBI("combi") }

@Composable
fun TabText(label: String, active: Boolean, activeColor: Color, inactiveColor: Color) {
    Text(
        label,
        color = if (active) activeColor else inactiveColor,
        fontFamily = Pretendard,
        fontWeight = FontWeight(500),
        fontSize = 16.sp
    )
}

@Composable
fun CommunityStatusTabs(selected: CommunityTab, onSelect: (CommunityTab) -> Unit) {
    val purple = MainPurple
    val grey = LightGreyText

    TabRow(
        selectedTabIndex = selected.ordinal,
        containerColor = Color.White,
        indicator = { positions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(positions[selected.ordinal]),
                color = purple
            )
        },
    ) {
        Tab(
            selected = selected == CommunityTab.CHAT,
            onClick = { onSelect(CommunityTab.CHAT) },
            text = { TabText("그룹채팅", selected == CommunityTab.CHAT, purple, grey) }
        )
        Tab(
            selected = selected == CommunityTab.COMBI,
            onClick = { onSelect(CommunityTab.COMBI) },
            text = { TabText("조합왕", selected == CommunityTab.COMBI, purple, grey) }
        )
    }
}