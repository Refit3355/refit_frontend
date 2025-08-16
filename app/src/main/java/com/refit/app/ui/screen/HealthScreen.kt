package com.refit.app.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.data.health.HealthRepo
import com.refit.app.ui.composable.health.DailyList
import com.refit.app.ui.composable.health.LoadingState
import com.refit.app.ui.viewmodel.health.HealthViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

/**
 * Health Connect 데이터를 보여주는 Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(vm: HealthViewModel = viewModel()) {
    val ctx = LocalContext.current
    val uiState by vm.uiState.collectAsState()

    // 권한 요청 런처
    val launcher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { _ ->
        vm.onPermissionGranted(ctx)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "[임시] Health Connect 개발 및 연동 확인용 화면입니다.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
        Spacer(Modifier.height(8.dp))

        // 컨트롤 영역: 조회 범위 선택, 가져오기 버튼
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .weight(0.65f)
            ) {
                OutlinedTextField(
                    value = "${uiState.days}일",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("조회 범위") },
                    singleLine = true,
                    minLines = 1,
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .align(Alignment.CenterVertically)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf(7, 30, 60, 90).forEach { d ->
                        DropdownMenuItem(
                            text = { Text("${d}일") },
                            onClick = {
                                expanded = false
                                vm.onDaysChanged(ctx, d)
                            }
                        )
                    }
                }
            }

            // 조회 버튼
            Button(
                onClick = {
                    if (!uiState.loading) {
                        if (!uiState.permissionGranted) {
                            launcher.launch(HealthRepo.readPerms)
                        } else {
                            vm.fetch(ctx)
                        }
                    }
                },
                modifier = Modifier
                    .weight(0.35f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B5BD3),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 6.dp
                )
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("불러오는 중...", fontWeight = FontWeight.Bold, color = Color.White)
                } else {
                    Text(
                        text = "기록 조회",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 상태 표시
        when {
            uiState.loading -> LoadingState()
            !uiState.permissionGranted -> {
                Text(
                    "권한이 필요합니다. 조회 버튼을 누른 후 권한 승인을 허용해주세요.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            uiState.error != null -> Text("오류: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            uiState.rows.isEmpty() -> Text("데이터가 없습니다.")
            else -> DailyList(uiState.rows)
        }
    }
}
