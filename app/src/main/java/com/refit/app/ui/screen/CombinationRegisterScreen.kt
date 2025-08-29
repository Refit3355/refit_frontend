package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.refit.app.data.combination.model.CreateCombinationRequest
import com.refit.app.data.combination.modelAndView.CombinationRegisterViewModel
import com.refit.app.data.local.search.SearchHistoryStore
import com.refit.app.data.product.model.Product
import com.refit.app.data.product.modelAndView.SearchViewModel
import com.refit.app.data.product.modelAndView.SearchViewModelFactory
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.launch

// -------------------------
// 조합 등록 화면
// -------------------------
@Composable
fun CombinationRegisterScreen(
    onSearchClick: () -> Unit = {},
    selectedProducts: List<Product> = emptyList(),
    vm: CombinationRegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit = {}
) {
    val uiState by vm.state.collectAsState()
    val scrollState = rememberScrollState()

    var type by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isTypeValid = type != null
    val isNameValid = name.isNotBlank()
    val isProductsValid = selectedProducts.size in 2..6
    val isDescriptionValid = description.length >= 10
    val isFormValid = isTypeValid && isNameValid && isProductsValid && isDescriptionValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("조합 등록", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(24.dp))

        // 타입 선택
        Text("조합 타입", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("뷰티", "헬스").forEach { option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .border(1.dp, if (type == option) MainPurple else Color.LightGray, RoundedCornerShape(8.dp))
                        .background(if (type == option) MainPurple.copy(alpha = 0.1f) else Color.White, RoundedCornerShape(8.dp))
                        .clickable { type = option },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        fontSize = 14.sp,
                        fontFamily = Pretendard,
                        fontWeight = if (type == option) FontWeight.Bold else FontWeight.Normal,
                        color = if (type == option) MainPurple else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 조합명
        Text("조합명", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("조합명 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 상품 선택
        Text("조합 상품 선택", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSearchClick() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MainPurple)
        ) {
            Text("상품 검색", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        selectedProducts.forEach { product ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontFamily = Pretendard,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 설명
        Text("조합설명", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("조합을 사용한 기간, 사용 후 변화 등을 자유롭게 작성해주세요!") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 등록 버튼
        Button(
            onClick = {
                val ids = selectedProducts.map { it.id.toLong() }
                val req = CreateCombinationRequest(
                    name = name,
                    content = description,
                    type = if (type == "뷰티") "beauty" else "health",
                    product1Id = ids[0],
                    product2Id = ids[1],
                    product3Id = ids.getOrNull(2),
                    product4Id = ids.getOrNull(3),
                    product5Id = ids.getOrNull(4),
                    product6Id = ids.getOrNull(5)
                )
                vm.registerCombination(req)
            },
            enabled = isFormValid && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isFormValid) MainPurple else Color.LightGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (uiState.isLoading) "등록 중..." else "등록하기", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }

    if (uiState.successId != null) {
        LaunchedEffect(uiState.successId) { onRegisterSuccess() }
    }
    if (uiState.error != null) {
        LaunchedEffect(uiState.error) { println("등록 실패: ${uiState.error}") }
    }
}