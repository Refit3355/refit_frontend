package com.refit.app.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.refit.app.data.myfit.model.MemberProductItem
import com.refit.app.data.myfit.model.MyfitCategory
import com.refit.app.data.myfit.model.MyfitDataSource
import com.refit.app.data.myfit.model.MyfitEffect
import com.refit.app.data.myfit.model.UpdateMemberProductRequest
import com.refit.app.data.myfit.repository.MyfitRepository
import com.refit.app.ui.composable.common.CategoryDropdown
import com.refit.app.ui.composable.common.SpinnerDatePicker
import com.refit.app.ui.composable.myfit.TypeSegment
import com.refit.app.ui.theme.GreyInput
import com.refit.app.ui.theme.MainPurple
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyfitEditScreen(
    item: MemberProductItem,
    navController: NavController? = null,
    repo: MyfitRepository = MyfitRepository()
) {
    val initialType = if (item.bhType == 0) MyfitDataSource.TYPE_BEAUTY else MyfitDataSource.TYPE_HEALTH
    val isInternal = item.productId != null // 내부 상품 여부

    // --- 상태 ---
    var type by remember { mutableStateOf(initialType) }             // 외부 상품만 변경 허용
    var productName by remember { mutableStateOf(item.productName) }
    var brandName by remember { mutableStateOf(item.brandName) }
    var recommendedDays by remember { mutableStateOf((item.recommendedPeriod ?: 0).toString()) }
    var startDate by remember { mutableStateOf(parseYmd(item.startDate) ?: LocalDate.now()) }

    // 타입별 카테고리/효과 목록
    val categoriesForType: List<MyfitCategory> = remember(type) { MyfitDataSource.categoriesFor(type) }
    val effectsForType: List<MyfitEffect> = remember(type) { MyfitDataSource.effectsFor(type) }

    // 선택 상태
    var selectedCategory by remember {
        mutableStateOf(
            // 초기 진입 시에는 서버 값으로 프리셀렉트 (타입이 동일할 때만)
            categoriesForType.firstOrNull { it.id.toLong() == item.categoryId && initialType == type }
        )
    }
    var selectedEffects by remember { mutableStateOf(item.effects.toSet()) } // Set<Long>

    // 타입이 바뀌면, 카테고리/효과를 해당 타입에 맞게 리셋
    LaunchedEffect(type) {
        selectedCategory =
            if (type == initialType) categoriesForType.firstOrNull { it.id.toLong() == item.categoryId }
            else null
        val keep = effectsForType.map { it.id.toLong() }.toSet()
        selectedEffects = selectedEffects.intersect(keep)
    }

    // 저장/에러 상태
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun isValidExternal(): Boolean {
        val days = recommendedDays.toIntOrNull() ?: 0
        return productName.isNotBlank() && brandName.isNotBlank() && days > 0 && selectedCategory != null
    }
    fun isValidInternal(): Boolean = (recommendedDays.toIntOrNull() ?: 0) > 0

    // 여백/토큰
    val PAGE_HP = 28.dp
    val SECTION_GAP = 26.dp
    val INNER_GAP = 6.dp
    val BOTTOM_ACTION_GAP = 36.dp

    val scroll = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scroll)
            .imePadding()
            .padding(horizontal = PAGE_HP, vertical = 27.dp),
        verticalArrangement = Arrangement.spacedBy(SECTION_GAP)
    ) {
        // ===== 내부/외부 분기 =====
        if (!isInternal) {
            // ---------- 외부 상품: 모든 필드 + 타입 변경 허용 ----------
            Section(title = "상품 타입", innerGap = INNER_GAP) {
                TypeSegment(
                    current = type,
                    onChange = { new ->
                        // 타입 바꾸면 카테고리/효과도 해당 타입으로 정리
                        type = new
                    },
                    spacing = 8.dp
                )
            }

            Section(title = "상품명", innerGap = INNER_GAP) {
                SmallOutlinedField(
                    value = productName,
                    onValueChange = { productName = it },
                    placeholder = "상품명 입력"
                )
            }

            Section(title = "브랜드명", innerGap = INNER_GAP) {
                SmallOutlinedField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    placeholder = "브랜드명 입력"
                )
            }

            Section(title = "권장 소비기한", innerGap = INNER_GAP) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallOutlinedField(
                        value = recommendedDays,
                        onValueChange = { s -> recommendedDays = s.filter { it.isDigit() } },
                        placeholder = "권장 소비기한 입력 (ex 90)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("일", style = MaterialTheme.typography.titleMedium, color = GreyInput, fontSize = 18.sp)
                }
            }

            Section(title = "상품 개봉일", innerGap = INNER_GAP) {
                SpinnerDatePicker(
                    initial = startDate,
                    onDateChange = { startDate = it },
                    pickerHeight = 104.dp,
                    dividerHeightPx = 2
                )
            }

            Section(title = "효과", innerGap = INNER_GAP) {
                EffectSectionedSelector(
                    type = type,
                    selected = selectedEffects,
                    onToggle = { id ->
                        selectedEffects =
                            if (selectedEffects.contains(id)) selectedEffects - id
                            else selectedEffects + id
                    }
                )
            }

            Section(title = "카테고리", innerGap = INNER_GAP) {
                CategoryDropdown(
                    items = categoriesForType,
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "카테고리 선택",
                    enabled = true
                )
            }

            Spacer(Modifier.height(BOTTOM_ACTION_GAP))

            if (error != null) Text("오류: $error", color = MaterialTheme.colorScheme.error)

            Button(
                onClick = {
                    val days = recommendedDays.toIntOrNull() ?: 0
                    val req = UpdateMemberProductRequest(
                        productName = productName.trim(),
                        brandName = brandName.trim(),
                        categoryId = selectedCategory?.id?.toLong(),
                        effectIds = selectedEffects.map { it.toLong() },
                        recommendedPeriod = days,
                        startDate = startDate.toString()
                    )
                    isLoading = true; error = null
                    scope.launch {
                        runCatching { repo.updateMemberProduct(item.memberProductId, req) }
                            .onSuccess {
                                isLoading = false
                                navController?.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("myfit_refresh", true)
                                navController?.popBackStack()
                            }
                            .onFailure { e -> isLoading = false; error = e.message ?: "수정 실패" }
                    }
                },
                enabled = !isLoading && isValidExternal(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    contentColor = Color.White,
                    disabledContainerColor = GreyInput.copy(alpha = 0.7f),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text(if (isLoading) "저장 중..." else "수정하기", fontSize = 18.sp) }
        } else {
            // ---------- 내부 상품: 바꿀 수 없는 값은 아예 보여주지 않기 ----------
            Section(title = "권장 소비기한", innerGap = INNER_GAP) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallOutlinedField(
                        value = recommendedDays,
                        onValueChange = { s -> recommendedDays = s.filter { it.isDigit() } },
                        placeholder = "권장 소비기한 입력 (ex 90)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("일", style = MaterialTheme.typography.titleMedium, color = GreyInput, fontSize = 18.sp)
                }
            }

            Section(title = "상품 개봉일", innerGap = INNER_GAP) {
                SpinnerDatePicker(
                    initial = startDate,
                    onDateChange = { startDate = it },
                    pickerHeight = 104.dp,
                    dividerHeightPx = 2
                )
            }

            Spacer(Modifier.height(BOTTOM_ACTION_GAP))

            if (error != null) Text("오류: $error", color = MaterialTheme.colorScheme.error)

            Button(
                onClick = {
                    val days = recommendedDays.toIntOrNull() ?: 0
                    val req = UpdateMemberProductRequest(
                        productName = null,            // 내부 상품은 수정 불가
                        brandName = null,
                        categoryId = null,
                        effectIds = null,
                        recommendedPeriod = days,
                        startDate = startDate.toString()
                    )
                    isLoading = true; error = null
                    scope.launch {
                        runCatching { repo.updateMemberProduct(item.memberProductId, req) }
                            .onSuccess {
                                isLoading = false
                                navController?.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("myfit_refresh", true)
                                navController?.popBackStack()
                            }
                            .onFailure { e -> isLoading = false; error = e.message ?: "수정 실패" }
                    }
                },
                enabled = !isLoading && isValidInternal(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    contentColor = Color.White,
                    disabledContainerColor = GreyInput.copy(alpha = 0.7f),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text(if (isLoading) "저장 중..." else "수정하기", fontSize = 18.sp) }
        }

        Spacer(Modifier.height(12.dp))
    }
}

/* ---------------------- 유틸/공통 블록 ---------------------- */

@Composable
private fun Section(
    title: String,
    innerGap: Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(innerGap)) {
        SectionTitle(title)
        content()
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun SmallOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = GreyInput, fontWeight = FontWeight.W300) },
        singleLine = singleLine,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 38.dp),
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(5.dp),
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MainPurple,
            unfocusedIndicatorColor = GreyInput,
            disabledIndicatorColor = GreyInput,
            cursorColor = GreyInput,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledTextColor = LocalContentColor.current
        )
    )
}

/** 효과 칩 선택 (피부/헤어/건강), 외부 상품에서 사용 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EffectSectionedSelector(
    type: String,
    selected: Set<Long>,
    onToggle: (Long) -> Unit
) {
    val sections: List<Pair<String, List<MyfitEffect>>> = remember(type) {
        MyfitDataSource.sectionedEffects(type)
    }

    val CHIP_HG = 8.dp
    val CHIP_VG = 0.dp
    val LABEL_W = 52.dp

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        sections.forEach { (title, list) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Spacer(Modifier.height(13.dp))
                    Text(
                        title,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.width(LABEL_W)
                    )
                }
                Spacer(Modifier.width(8.dp))
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(CHIP_HG),
                    verticalArrangement = Arrangement.spacedBy(CHIP_VG)
                ) {
                    list.forEach { e ->
                        val key = e.id.toLong()
                        val isOn = selected.contains(key)
                        Surface(
                            onClick = { onToggle(key) },
                            shape = RoundedCornerShape(50),
                            color = if (isOn) MainPurple else Color(0xFFF2F2F2),
                            border = null,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp
                        ) {
                            Text(
                                text = e.name,
                                color = if (isOn) Color(0xFFFFFFFF) else Color(0xFF000000),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(5.dp))
        }
    }
}

/** YYYY-MM-DD → LocalDate */
@RequiresApi(Build.VERSION_CODES.O)
private fun parseYmd(s: String?): LocalDate? =
    try { if (s.isNullOrBlank()) null else LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE) }
    catch (_: DateTimeParseException) { null }
