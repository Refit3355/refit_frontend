package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.refit.app.R
import com.refit.app.ui.composable.auth.ChipGroupMulti
import com.refit.app.ui.composable.auth.ChipGroupSingle
import com.refit.app.ui.composable.auth.SectionHeader
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.imePadding

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
    
    val hasAllSelected =
        selectedSkinType != null &&
                selectedSkinConcerns.isNotEmpty() &&
                selectedScalpConcerns.isNotEmpty() &&
                selectedHealthConcerns.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFE5E5EA))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(2f / 3f)
                    .background(MainPurple)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scroll)
                .imePadding()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
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

            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = onNextOrSubmit,
                enabled = submitEnabled && hasAllSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE5E5EA),
                    disabledContentColor = Color(0xFF9E9E9E)
                )
            ) {
                Text(
                    text = "가입하기",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Pretendard
                    )
                )
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun Preview_SignupStep2Screen() {
    var skinType by remember { mutableStateOf<String?>(null) }
    var skinConcerns by remember { mutableStateOf(setOf<String>()) }
    var scalpConcerns by remember { mutableStateOf(setOf<String>()) }
    var healthConcerns by remember { mutableStateOf(setOf<String>()) }

    MaterialTheme {
        SignupStep2Screen(
            selectedSkinType = skinType,
            selectedSkinConcerns = skinConcerns,
            selectedScalpConcerns = scalpConcerns,
            selectedHealthConcerns = healthConcerns,
            onSkinTypeChange = { skinType = it },
            onToggleSkinConcern = { opt ->
                skinConcerns = if (opt in skinConcerns) skinConcerns - opt else skinConcerns + opt
            },
            onToggleScalpConcern = { opt ->
                scalpConcerns = if (opt in scalpConcerns) scalpConcerns - opt else scalpConcerns + opt
            },
            onToggleHealthConcern = { opt ->
                healthConcerns = if (opt in healthConcerns) healthConcerns - opt else healthConcerns + opt
            },
            onBack = {},
            onNextOrSubmit = {},
            submitEnabled = true
        )
    }
}
