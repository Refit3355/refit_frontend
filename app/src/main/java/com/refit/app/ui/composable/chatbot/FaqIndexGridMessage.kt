@file:OptIn(ExperimentalLayoutApi::class)

package com.refit.app.ui.composable.chatbot

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun FaqIndexGridMessage(
    onPick: (FaqEntry) -> Unit,
    resetKey: Any? = null
) {
    var tab by remember(resetKey) { mutableStateOf(0) }
    var expanded by remember(resetKey) { mutableStateOf(false) }

    val cosmeticsRange = 1..6
    val supplementsRange = 7..14

    val base = remember(tab, resetKey) {
        when (tab) {
            0 -> FaqIndex.entries.filter { it.id.removePrefix("faq_q").toIntOrNull() in cosmeticsRange }
            else -> FaqIndex.entries.filter { it.id.removePrefix("faq_q").toIntOrNull() in supplementsRange }
        }
    }
    val shown = if (expanded) base else base.take(5)

    val cardShape = RoundedCornerShape(16.dp)

    Surface(
        shape = cardShape,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 헤더
            Surface(
                color = MainPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "자주 묻는 질문",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight(500),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.weight(1f))

                    val deg by animateFloatAsState(if (expanded) 180f else 0f, label = "expand-rotate")
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Outlined.ExpandMore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.rotate(deg)
                        )
                    }
                }
            }

            // Segmented Control (탭)
            UnderlineTabs(
                selected = tab,
                onSelect = {
                    tab = it
                    expanded = false
                },
                labels = listOf("뷰티", "헬스")
            )

            Spacer(Modifier.height(12.dp))

            // FAQ 리스트
            FaqList(
                items = shown,
                categoryLabelProvider = { e -> if (tab == 0) "화장품" else "영양제" },
                onPick = onPick
            )
        }
    }
}

@Composable
fun UnderlineTabs(
    selected: Int,
    onSelect: (Int) -> Unit,
    labels: List<String>
) {
    val indicatorColor = MainPurple
    val textSelected = MainPurple
    val textUnselected = Color(0xFF9E9E9E)

    TabRow(
        selectedTabIndex = selected,
        containerColor = Color.Transparent,
        contentColor = indicatorColor,
        divider = {
            // 하단 얇은 구분선
            Divider(color = Color(0xFFDFE2E6), thickness = 1.dp)
        },
        indicator = { tabPositions ->
            val current = tabPositions[selected]
            Box(
                Modifier
                    .tabIndicatorOffset(current)
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(indicatorColor)
            )
        }
    ) {
        labels.forEachIndexed { index, label ->
            val isSelected = index == selected
            Tab(
                selected = isSelected,
                onClick = { onSelect(index) },
                selectedContentColor = textSelected,
                unselectedContentColor = textUnselected,
                text = {
                    Text(
                        text = label,
                        fontFamily = Pretendard,
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500)
                    )
                }
            )
        }
    }
}

@Composable
private fun FaqList(
    items: List<FaqEntry>,
    categoryLabelProvider: (FaqEntry) -> String,
    onPick: (FaqEntry) -> Unit
) {
    val dividerColor = Color(0xFFECECEC)

    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, e ->
            FaqListItem(
                question = e.question,
                onClick = { onPick(e) }
            )
            if (index != items.lastIndex) {
                Divider(
                    color = dividerColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun FaqListItem(
    question: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = question,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF111111),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    clickable(interactionSource = interaction, indication = null) { onClick() }
}
