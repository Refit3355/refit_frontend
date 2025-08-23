package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.data.auth.modelAndView.HealthEditViewModel
import com.refit.app.ui.composable.auth.ChipGroupMulti
import com.refit.app.ui.composable.auth.ChipGroupSingle
import com.refit.app.ui.composable.auth.SectionHeader
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import com.refit.app.R

object HealthEditOptions {
    val skinTypes      = listOf("건성","중성","지성","복합성","수부지")
    val skinConcerns   = listOf("아토피","여드름/민감성","미백/잡티","피지/블랙헤드","속건조","주름/탄력","모공","홍조","각질","해당없음")
    val scalpConcerns  = listOf("탈모","손상모","두피트러블","비듬/각질","해당없음")
    val healthConcerns = listOf("눈건강","만성피로","수면/스트레스","면역력","근력","장건강","혈액순환","해당없음")
}

@Composable
fun HealthEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: HealthEditViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    LaunchedEffect(Unit) { vm.prefillFromPrefs() }

    Scaffold(
        bottomBar = {
            Button(
                onClick = { vm.save(onSaved, onError = { /* TODO: 스낵바 */ }) },
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
            ) {
                Text(
                    "완료",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Pretendard
                    )
                )
            }
        },
        containerColor = Color.White
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(pad)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    SectionHeader(
                        title = "피부 타입",
                        iconResId = R.drawable.ic_skin_signup,
                        iconTint = MainPurple
                    )
                    ChipGroupSingle(
                        options = HealthEditOptions.skinTypes,
                        selected = vm.skinType,
                        onChange = vm::updateSkinType
                    )

                    SectionHeader(
                        title = "피부 고민",
                        iconResId = R.drawable.ic_skin_concern,
                        iconTint = MainPurple
                    )
                    ChipGroupMulti(
                        options = HealthEditOptions.skinConcerns,
                        selected = vm.skinConcerns,
                        onToggle = vm::toggleSkin,
                        exclusiveOption = "해당없음"
                    )

                    SectionHeader(
                        title = "두피/모발 고민",
                        iconResId = R.drawable.ic_hair_concern,
                        iconTint = MainPurple
                    )
                    ChipGroupMulti(
                        options = HealthEditOptions.scalpConcerns,
                        selected = vm.scalpConcerns,
                        onToggle = vm::toggleScalp,
                        exclusiveOption = "해당없음"
                    )

                    SectionHeader(
                        title = "건강 고민",
                        iconResId = R.drawable.ic_health_concern,
                        iconTint = MainPurple
                    )
                    ChipGroupMulti(
                        options = HealthEditOptions.healthConcerns,
                        selected = vm.healthConcerns,
                        onToggle = vm::toggleHealth,
                        exclusiveOption = "해당없음"
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 1000)
@Composable
private fun HealthEditScreenPreview() {
    MaterialTheme {
        // 미리보기에서는 ViewModel 없이 간단히 상태 흉내
        var skinType by remember { mutableStateOf<String?>(null) }
        var skinC by remember { mutableStateOf(setOf<String>()) }
        var scalpC by remember { mutableStateOf(setOf<String>()) }
        var healthC by remember { mutableStateOf(setOf<String>()) }

        // Compose Preview 용 간이 화면
        Scaffold(
            bottomBar = {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 0.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainPurple, contentColor = Color.White
                    )
                ) { Text("완료",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Pretendard,
                    ))
                }
            },
            containerColor = Color.White
        ) { pad ->
            Column(
                Modifier
                    .padding(pad)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SectionHeader(
                    title = "피부 타입",
                    iconResId = R.drawable.ic_skin_signup,
                    iconTint = MainPurple
                )
                ChipGroupSingle(HealthEditOptions.skinTypes, skinType) { skinType = it }

                Spacer(Modifier.height(12.dp))

                SectionHeader(
                    title = "피부 고민",
                    iconResId = R.drawable.ic_skin_concern,
                    iconTint = MainPurple
                )
                ChipGroupMulti(HealthEditOptions.skinConcerns, skinC, {
                    skinC = if (it in skinC) skinC - it else skinC + it
                }, "해당없음")

                Spacer(Modifier.height(12.dp))

                SectionHeader(
                    title = "두피/모발 고민",
                    iconResId = R.drawable.ic_hair_concern,
                    iconTint = MainPurple
                )
                ChipGroupMulti(HealthEditOptions.scalpConcerns, scalpC, {
                    scalpC = if (it in scalpC) scalpC - it else scalpC + it
                }, "해당없음")

                Spacer(Modifier.height(12.dp))

                SectionHeader(
                    title = "건강 고민",
                    iconResId = R.drawable.ic_health_concern,
                    iconTint = MainPurple
                )
                ChipGroupMulti(HealthEditOptions.healthConcerns, healthC, {
                    healthC = if (it in healthC) healthC - it else healthC + it
                }, "해당없음")
            }
        }
    }
}