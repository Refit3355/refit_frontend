package com.refit.app.ui.composable.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.Pretendard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BotTemplateBubble(
    templateId: String,
    variables: Map<String, String> = emptyMap(),
    onUserReply: (String) -> Unit = {},
    onNext: (String) -> Unit = {},
    onSetVars: (Map<String, String>) -> Unit = {},
    onDeeplink: (String) -> Unit = {}
) {
    val ctx = LocalContext.current
    val bundle = remember { TemplateRepository.loadFromAssets(ctx) }
    val tpl = remember(templateId, bundle) { bundle.get(templateId) } ?: return

    val (footerChips, inlineChips) = remember(tpl) {
        val isFooter: (ChipItem) -> Boolean = { it.placement.equals("footer", ignoreCase = true) }
        val footers = tpl.chips.filter(isFooter)
        val inlines = tpl.chips.filterNot(isFooter)
        footers to inlines
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_jellbbo_chatbot_profile),
            contentDescription = "챗봇 프로필",
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(8.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "젤뽀",
                fontSize = 14.sp,
                fontFamily = Pretendard,
                fontWeight = FontWeight(500),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 8.dp, topEnd = 16.dp,
                        bottomEnd = 16.dp, bottomStart = 16.dp
                    ),
                    modifier = Modifier.widthIn(max = 260.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        // 본문 블록
                        tpl.blocks.forEachIndexed { idx, raw ->
                            MarkdownBold(text = raw.interpolate(variables))
                            if (idx != tpl.blocks.lastIndex) Spacer(Modifier.height(6.dp))
                        }

                        // 불릿
                        if (tpl.bullets.isNotEmpty()) Spacer(Modifier.height(8.dp))
                        tpl.bullets.forEach { b ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", style = MaterialTheme.typography.bodySmall, color = Color(0xFF111111))
                                MarkdownBold(
                                    text = b.interpolate(variables)
                                )
                            }
                        }

                        // 칩(하단 선택)
                        if (inlineChips.isNotEmpty()) Spacer(Modifier.height(12.dp))
                        if (tpl.id != "service_overview") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                inlineChips.forEach { chip ->
                                    ChatChoiceChip(
                                        label = chip.label.interpolate(variables),
                                        onClick = {
                                            onUserReply(chip.label.interpolate(variables))
                                            when {
                                                chip.next != null     -> onNext(chip.next)
                                                chip.deeplink != null -> onDeeplink(chip.deeplink!!.interpolate(variables))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (footerChips.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    footerChips.forEach { chip ->
                        FooterChip(
                            label = chip.label.interpolate(variables),
                            onClick = {
                                val label = chip.label.interpolate(variables)
                                onUserReply(label)

                                if (chip.next == "reco_start" && chip.value != null) {
                                    val bhType = if (tpl.id == "reco_health_select") 1 else 0
                                    val effectId = chip.value.toString()
                                    onSetVars(mapOf(
                                        "bhType" to bhType.toString(),
                                        "effectId" to effectId
                                    ))
                                    onNext("reco_hint")
                                } else {
                                    when {
                                        chip.next != null -> onNext(chip.next)
                                        chip.deeplink != null -> {
                                            val route = chip.deeplink!!.interpolate(variables)
                                            if (route.isNotBlank()) onDeeplink(route)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }


        }

        Spacer(Modifier.width(12.dp))

    }
}

private fun String.interpolate(vars: Map<String, String>): String {
    var s = this
    vars.forEach { (k, v) -> s = s.replace("{$k}", v) }
    return s
}

@Composable
private fun MarkdownBold(text: String) {
    val annotated = remember(text) {
        val parts = text.split("**")
        buildAnnotatedString {
            parts.forEachIndexed { i, part ->
                if (i % 2 == 1) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(part) }
                else append(part)
            }
        }
    }
    Text(
        annotated,
        fontFamily = Pretendard,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = Color(0xFF111111)
    )
}
