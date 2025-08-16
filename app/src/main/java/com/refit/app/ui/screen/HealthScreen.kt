package com.refit.app.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import com.refit.app.health.DailyRow
import com.refit.app.health.HealthRepo
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// 중복 실행 가드
private var isFetching = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var days by remember { mutableStateOf(30) }
    var rows by remember { mutableStateOf<List<DailyRow>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }

    // 권한 요청 런처
    val launcher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { _ ->
        permissionGranted = true
        scope.launch {
            fetch(
                ctx, days,
                onStart = { loading = true; error = null },
                onDone = { r -> rows = r; loading = false },
                onError = { e -> error = e; loading = false }
            )
        }
    }

    // 최초 진입 시 권한 체크
    LaunchedEffect(Unit) {
        val client = HealthRepo.client(ctx)
        val granted = client.permissionController.getGrantedPermissions()
        if (!granted.containsAll(HealthRepo.readPerms)) {
            permissionGranted = false
        } else {
            permissionGranted = true
            fetch(
                ctx, days,
                onStart = { loading = true; error = null },
                onDone = { r -> rows = r; loading = false },
                onError = { e -> error = e; loading = false }
            )
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Health Connect 미리보기",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        // 컨트롤 영역
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 일수 선택
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = "${days}일",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("조회 범위") },
                    modifier = Modifier.menuAnchor().weight(1f)
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
                                days = d
                                if (permissionGranted && !loading) {
                                    scope.launch {
                                        fetch(
                                            ctx, days,
                                            onStart = { loading = true; error = null },
                                            onDone = { r -> rows = r; loading = false },
                                            onError = { e -> error = e; loading = false }
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // 새로고침 버튼
            Button(
                onClick = {
                    if (loading) return@Button
                    if (!permissionGranted) {
                        launcher.launch(HealthRepo.readPerms)
                    } else {
                        scope.launch {
                            fetch(
                                ctx, days,
                                onStart = { loading = true; error = null },
                                onDone = { r -> rows = r; loading = false },
                                onError = { e -> error = e; loading = false }
                            )
                        }
                    }
                }
            ) { Text(if (loading) "불러오는 중…" else "가져오기") }
        }

        Spacer(Modifier.height(12.dp))

        // 상태 표시
        when {
            loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("불러오는 중...")
                }
            }
            !permissionGranted -> {
                Text(
                    "권한이 필요합니다. “가져오기”를 눌러 권한을 허용하세요.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            error != null -> Text("오류: $error", color = MaterialTheme.colorScheme.error)
            rows.isEmpty() -> Text("데이터가 없습니다.")
            else -> DailyList(rows)
        }
    }
}

private suspend fun fetch(
    ctx: android.content.Context,
    days: Int,
    onStart: () -> Unit,
    onDone: (List<DailyRow>) -> Unit,
    onError: (String) -> Unit
) {
    if (isFetching) return
    try {
        isFetching = true
        onStart()
        val r = HealthRepo.readDailyAll(ctx, days.toLong())
        onDone(r)
    } catch (e: Exception) {
        val msg = e.message ?: e.toString()
        val pretty = if (
            msg.contains("rate limited", ignoreCase = true) ||
            msg.contains("quota", ignoreCase = true) ||
            msg.contains("request rejected", ignoreCase = true)
        ) "요청이 너무 빠르게 전송되었습니다. 잠시 후 다시 시도해 주세요."
        else msg
        onError(pretty)
    } finally {
        isFetching = false
    }
}

@Composable
private fun DailyList(rows: List<DailyRow>) {
    val fmt = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(rows) { row ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        row.date.format(fmt),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))

                    InfoLine("걸음수(steps)", row.steps?.toString())
                    InfoLine("운동칼로리(active kcal)", row.activeKcal?.round1())

                    val bp = when {
                        row.systolicMmhg != null && row.diastolicMmhg != null ->
                            "${row.systolicMmhg.round1()}/${row.diastolicMmhg.round1()} mmHg"
                        row.systolicMmhg != null -> "${row.systolicMmhg.round1()} mmHg"
                        row.diastolicMmhg != null -> "${row.diastolicMmhg.round1()} mmHg"
                        else -> null
                    }
                    InfoLine("혈압(blood pressure)", bp)

                    val mgdl = row.bloodGlucoseMgdl
                    val mmol = mgdl?.let { it * 0.0555 }
                    val glucoseText = when (mgdl) {
                        null -> null
                        else -> "${mgdl.round1()} mg/dL (${mmol?.round2()} mmol/L)"
                    }
                    InfoLine("혈당(blood glucose)", glucoseText)

                    InfoLine("영양 섭취(nutrition kcal)", row.intakeKcal?.round1())
                    val sleep = row.sleepMinutes?.let { "${it} min (${(it / 60.0).round1()} h)" }
                    InfoLine("수면(sleep)", sleep)
                }
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String?) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value ?: "-", fontWeight = FontWeight.SemiBold)
    }
}

private fun Double.round1(): String =
    ((this * 10).roundToInt() / 10.0).toString()

private fun Double.round2(): String =
    ((this * 100).roundToInt() / 100.0).toString()
