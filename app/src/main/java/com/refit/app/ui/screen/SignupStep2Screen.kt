package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.composable.auth.ChipGroupMulti
import com.refit.app.ui.composable.auth.ChipGroupSingle
import com.refit.app.ui.composable.auth.SectionHeader
import com.refit.app.ui.composable.auth.SignupTopBar
import com.refit.app.ui.theme.MainPurple

object SignupOptions {
    val skinTypes = listOf("건성", "중성", "지성", "복합성", "수부지")
    val skinConcerns = listOf("아토피","여드름/민감성","미백/잡티","피지/블랙헤드","속건조","주름/탄력","모공","홍조","각질","해당없음")
    val scalpConcerns = listOf("탈모","손상모","두피트러블","비듬/각질","해당없음")
    val healthConcerns = listOf("눈건강","만성피로","수면/스트레스","면역력","근력","장건강","혈액순환","해당없음")
}

@Composable
fun SignupStep2Screen(
    selectedSkinType: String?,
    selectedSkinConcerns: Set<String>,
    selectedScalpConcerns: Set<String>,
    selectedHealthConcerns: Set<String>,
    onSkinTypeChange: (String?) -> Unit,
    onToggleSkinConcern: (String) -> Unit,
    onToggleScalpConcern: (String) -> Unit,
    onToggleHealthConcern: (String) -> Unit,
    onBack: () -> Unit,
    onNextOrSubmit: () -> Unit,
    submitEnabled: Boolean
) {
    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            SignupTopBar(
                title = "회원가입",
                stepIndex = 2,
                stepCount = 3,
                onBack = onBack
            )
        },
        bottomBar = {
            Button(
                onClick = onNextOrSubmit,
                enabled = submitEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding()
                    .height(60.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    contentColor = Color.White,
                    disabledContainerColor = MainPurple.copy(alpha = 0.4f),
                    disabledContentColor = Color.White
                )
            ) { Text(
                text = "가입하기",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            ) }
        },
        containerColor = Color.White
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(pad)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val headerTextStyle = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                lineHeight = 26.sp
            )
            val autoIconSize = with(LocalDensity.current) { headerTextStyle.fontSize.toDp() }
            Text(
                text = buildAnnotatedString {
                    append("더 ")
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                        append("정확한 정보")
                    }
                    append("를 드리기 위해,\n사용자님을 조금 더 알고 싶어요!")
                },
                style = headerTextStyle,
                color = Color(0xFF4A4A4A),
                fontSize = 25.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = autoIconSize),
                textAlign = TextAlign.Start
            )

            // 본문 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SectionHeader(
                        title = "피부 타입",
                        iconResId = R.drawable.ic_skin_signup,
                        iconTint = MainPurple
                    )
                    ChipGroupSingle(
                        options = SignupOptions.skinTypes,
                        selected = selectedSkinType,
                        onChange = onSkinTypeChange
                    )

                    SectionHeader(
                        title = "피부 고민",
                        iconResId = R.drawable.ic_skin_concern,
                        iconTint = MainPurple
                    )
                    ChipGroupMulti(
                        options = SignupOptions.skinConcerns,
                        selected = selectedSkinConcerns,
                        onToggle = onToggleSkinConcern,
                        exclusiveOption = "해당없음"
                    )

                    SectionHeader(
                        title = "두피/모발 고민",
                        iconResId = R.drawable.ic_hair_concern,
                        iconTint = MainPurple
                    )
                    ChipGroupMulti(
                        options = SignupOptions.scalpConcerns,
                        selected = selectedScalpConcerns,
                        onToggle = onToggleScalpConcern,
                        exclusiveOption = "해당없음"
                    )

                    SectionHeader(
                        title = "건강 고민",
                        iconResId = R.drawable.ic_health_concern,
                        iconTint = MainPurple
                    )
                    ChipGroupMulti(
                        options = SignupOptions.healthConcerns,
                        selected = selectedHealthConcerns,
                        onToggle = onToggleHealthConcern,
                        exclusiveOption = "해당없음"
                    )
                }
            }

            // 스크롤 끝에 살짝 여백 (하단 버튼과 겹치지 않게)
            Spacer(Modifier.height(8.dp))
        }
    }
}


@Preview(showBackground = true, widthDp = 420, heightDp = 1000)
@Composable
private fun PreviewSignupStep2Hoisted() {
    MaterialTheme {
        var skinType by remember { mutableStateOf<String?>(null) }
        var skinC by remember { mutableStateOf(setOf<String>()) }
        var scalpC by remember { mutableStateOf(setOf<String>()) }
        var healthC by remember { mutableStateOf(setOf<String>()) }

        fun toggle(set: Set<String>, item: String): Set<String> =
            if (item in set) set - item else set + item

        SignupStep2Screen(
            selectedSkinType = skinType,
            selectedSkinConcerns = skinC,
            selectedScalpConcerns = scalpC,
            selectedHealthConcerns = healthC,
            onSkinTypeChange = { skinType = it },
            onToggleSkinConcern = { skinC = toggle(skinC, it) },
            onToggleScalpConcern = { scalpC = toggle(scalpC, it) },
            onToggleHealthConcern = { healthC = toggle(healthC, it) },
            onBack = {},
            onNextOrSubmit = {},
            submitEnabled = (skinType != null)
        )
    }
}