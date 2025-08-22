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
import com.refit.app.data.myfit.model.CreateMemberProductRequest
import com.refit.app.data.myfit.model.MyfitCategory
import com.refit.app.data.myfit.model.MyfitEffect
import com.refit.app.data.myfit.model.MyfitDataSource
import com.refit.app.data.myfit.repository.MyfitRepository
import com.refit.app.ui.composable.common.SpinnerDatePicker
import com.refit.app.ui.composable.myfit.TypeSegment
import com.refit.app.ui.composable.common.CategoryDropdown
import com.refit.app.ui.theme.GreyOutline
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.GreyInput
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyfitRegisterScreen(
    navController: NavController? = null,
    repo: MyfitRepository = MyfitRepository()
) {
    // 상태
    var type by remember { mutableStateOf("beauty") }
    var productName by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") }
    var recommendedDays by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }

    val categoriesForType by remember(type) { mutableStateOf(MyfitDataSource.categoriesFor(type)) }
    var selectedCategory by remember { mutableStateOf<MyfitCategory?>(null) }

    var selectedEffects by remember { mutableStateOf<Set<Int>>(emptySet()) }
    LaunchedEffect(type) {
        selectedCategory = null
        val keep = MyfitDataSource.effectsFor(type).map { it.id }.toSet()
        selectedEffects = selectedEffects intersect keep
    }

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun isValid(): Boolean =
        productName.isNotBlank() &&
                brandName.isNotBlank() &&
                (recommendedDays.toIntOrNull() ?: 0) > 0 &&
                (selectedCategory != null)

    // 여백/토큰
    val PAGE_HP = 28.dp
    val SECTION_GAP = 26.dp           // 블록(제목+입력) 사이 간격: 큼
    val INNER_GAP  = 6.dp             // 블록 내부(제목↔입력) 간격: 작게
    val BOTTOM_ACTION_GAP = 36.dp     // 카테고리 ↔ 등록 버튼 간격

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
        // 상품 타입
        Section(title = "상품 타입", innerGap = INNER_GAP) {
            TypeSegment(current = type, onChange = { type = it }, spacing = 8.dp)
        }

        // 상품명
        Section(title = "상품명", innerGap = INNER_GAP) {
            SmallOutlinedField(
                value = productName,
                onValueChange = { productName = it },
                placeholder = "상품명 입력"
            )
        }

        // 브랜드명
        Section(title = "브랜드명", innerGap = INNER_GAP) {
            SmallOutlinedField(
                value = brandName,
                onValueChange = { brandName = it },
                placeholder = "브랜드명 입력"
            )
        }

        // 권장 소비기한
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
                Text("일", style = MaterialTheme.typography.titleMedium, color = GreyInput,
                    fontSize = 18.sp)
            }
        }

        // 상품 개봉일
        Section(title = "상품 개봉일", innerGap = INNER_GAP) {
            SpinnerDatePicker(
                initial = startDate,
                onDateChange = { startDate = it },
                pickerHeight = 104.dp,
                dividerHeightPx = 2
            )
        }

        // 효과
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

        // 카테고리
        Section(title = "카테고리", innerGap = INNER_GAP) {
            CategoryDropdown(
                items = categoriesForType,
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(BOTTOM_ACTION_GAP))

        if (error != null) Text("오류: $error", color = MaterialTheme.colorScheme.error)

        Button(
            onClick = {
                val req = CreateMemberProductRequest(
                    type = type,
                    productName = productName.trim(),
                    brandName = brandName.trim(),
                    recommendedPeriodDays = recommendedDays.toInt(),
                    startDate = startDate.toString(),
                    categoryId = selectedCategory!!.id.toLong(),
                    effect = selectedEffects.toList().ifEmpty { null }
                )
                isLoading = true; error = null
                scope.launch {
                    runCatching { repo.createCustom(req) }
                        .onSuccess { isLoading = false; navController?.popBackStack() }
                        .onFailure { e -> isLoading = false; error = e.message ?: "등록 실패" }
                }
            },
            enabled = !isLoading && isValid(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainPurple,
                contentColor = Color.White,
                disabledContainerColor = GreyInput.copy(alpha = 0.7f), // 비활성 배경
                disabledContentColor = Color.White    // 비활성 텍스트/아이콘
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (isLoading) "등록 중..." else "등록하기", fontSize = 18.sp
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

// 블록 래퍼: 제목↔입력(작은 간격), 블록 간(큰 간격)
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

// 효과 섹션
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EffectSectionedSelector(
    type: String, selected: Set<Int>, onToggle: (Int) -> Unit
) {
    val sections = remember(type) { MyfitDataSource.sectionedEffects(type) }

    val CHIP_HG = 8.dp
    val CHIP_VG = 0.dp
    val LABEL_W = 52.dp

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        sections.forEach { (title, list) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column() {
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
                        val isOn = selected.contains(e.id)
                        Surface(
                            onClick = { onToggle(e.id) },
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


@Composable
private fun SmallOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
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
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MainPurple,
            unfocusedIndicatorColor = GreyInput,
            cursorColor = GreyInput,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        )
    )
}


@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
}