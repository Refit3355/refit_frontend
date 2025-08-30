package com.refit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.refit.app.R
import com.refit.app.data.combination.model.CreateCombinationRequest
import com.refit.app.data.combination.modelAndView.CombinationRegisterViewModel
import com.refit.app.data.product.model.Product
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.compose.ui.graphics.Color
import com.refit.app.ui.composable.combiking.CombinationTypeChangeDialog
import kotlinx.coroutines.launch

@Composable
fun CombinationRegisterScreen(
    navController: NavController,
    selectedProducts: List<Product> = emptyList(),
    onSearchClick: (String) -> Unit = {},
    vm: CombinationRegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit = {}
) {
    val uiState by vm.state.collectAsState()
    val scrollState = rememberScrollState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val savedProducts =
        backStackEntry?.savedStateHandle?.getStateFlow("selectedProducts", emptyList<Product>())
            ?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    // 타입 상태
    var type by rememberSaveable { mutableStateOf<String?>(null) }

    // 상품 리스트 상태
    var products by remember { mutableStateOf(selectedProducts) }
    LaunchedEffect(savedProducts.value) {
        products = savedProducts.value
    }

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    // 모달 상태
    var showDialog by remember { mutableStateOf(false) }
    var pendingType by remember { mutableStateOf<String?>(null) }

    val isTypeValid = type != null
    val isNameValid = name.isNotBlank()
    val isProductsValid = products.size in 2..6
    val isDescriptionValid = description.length >= 10
    val isFormValid = isTypeValid && isNameValid && isProductsValid && isDescriptionValid

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 0.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // 타입 선택
            Text(
                "조합 타입",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Pretendard
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("뷰티", "헬스").forEach { option ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .border(
                                1.dp,
                                if (type == option) MainPurple else Color.LightGray,
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (type == option) MainPurple.copy(alpha = 0.1f) else Color.White,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                if (type == null || type == option) {
                                    type = option
                                } else {
                                    pendingType = option
                                    showDialog = true
                                }
                            },
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
            Text(
                "조합명",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Pretendard
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("조합명 입력", fontFamily = Pretendard) },
                modifier = Modifier.fillMaxWidth()
            )
            if (name.isBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_exclamation_fill),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "조합명은 필수입니다.",
                        fontSize = 12.sp,
                        color = MainPurple,
                        fontFamily = Pretendard
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 상품 선택
            Text(
                "조합 상품 선택",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Pretendard
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val bh = if (type == "뷰티") "beauty" else "health"
                    onSearchClick(bh)
                },
                enabled = type != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type != null) MainPurple else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "상품 검색",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Pretendard
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            products.forEach { product ->
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

            Spacer(modifier = Modifier.height(18.dp))

            // 조합 설명
            Text(
                "조합 설명",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Pretendard
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text(
                        "조합을 사용한 기간, 사용 후 변화 등을 자유롭게 작성해주세요!",
                        fontFamily = Pretendard
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            if (description.isBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_exclamation_fill),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "조합 설명은 필수입니다.",
                        fontSize = 12.sp,
                        color = MainPurple,
                        fontFamily = Pretendard
                    )
                }
            } else if (description.length in 1..9) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_exclamation_fill),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "설명에는 최소 10자 이상을 남겨주세요!",
                        fontSize = 12.sp,
                        color = MainPurple,
                        fontFamily = Pretendard
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 등록 버튼
            Button(
                onClick = {
                    val ids = products.map { it.id.toLong() }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) MainPurple else Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    if (uiState.isLoading) "등록 중..." else "등록하기",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Pretendard
                )
            }
        }
    }

    // 등록 성공 처리
    if (uiState.successId != null) {
        LaunchedEffect(uiState.successId) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("combination_result", "[$name] 조합 등록이 완료되었습니다!")

            onRegisterSuccess()
        }
    }

    // 등록 실패 처리
    if (uiState.error != null) {
        LaunchedEffect(uiState.error) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("combination_result", "등록 실패: ${uiState.error}")

            onRegisterSuccess()
        }
    }

    // 타입 변경 모달
    if (showDialog && pendingType != null) {
        CombinationTypeChangeDialog(
            onDismiss = {
                showDialog = false
                pendingType = null
            },
            onConfirm = {
                type = pendingType
                pendingType = null
                products = emptyList()
                showDialog = false
            }
        )
    }
}
