package com.refit.app.ui.composable.common

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.refit.app.ui.screen.CartScreen
import com.refit.app.ui.screen.CategoryScreen
import com.refit.app.ui.screen.CommunityScreen
import com.refit.app.ui.screen.HomeScreen
import com.refit.app.ui.screen.LoginScreen
import com.refit.app.ui.screen.MyfitEditScreen
import com.refit.app.ui.screen.MyfitRegisterScreen
import com.refit.app.ui.screen.MyfitScreen
import com.refit.app.ui.screen.NotificationScreen
import com.refit.app.ui.screen.ProductDetailScreen
import com.refit.app.ui.screen.RecommendationScreen
import com.refit.app.ui.screen.SearchScreen
import com.refit.app.ui.screen.SleepDetailScreen
import com.refit.app.ui.screen.StepsDetailScreen
import com.refit.app.ui.screen.WeatherDetailScreen
import com.refit.app.ui.screen.SignupStep1Screen
import com.refit.app.ui.screen.SignupStep2Screen
import com.refit.app.ui.screen.SignupStep3Screen
import com.refit.app.ui.screen.SplashScreen
import com.refit.app.ui.screen.WishScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.refit.app.data.analysis.modelAndView.AnalysisViewModel
import com.refit.app.data.analysis.modelAndView.AnalysisViewModelFactory
import com.refit.app.data.auth.modelAndView.FormMode
import com.refit.app.data.auth.modelAndView.KakaoFlowStore
import com.refit.app.data.myfit.viewmodel.MyfitViewModel
import com.refit.app.data.auth.modelAndView.SignupViewModel
import com.refit.app.ui.screen.ChatRoomScreen
import com.refit.app.data.product.model.Product
import com.refit.app.ui.screen.AnalysisResultScreen
import com.refit.app.ui.screen.AnalysisScreen
import com.refit.app.ui.screen.AnalysisUiState
import com.refit.app.ui.screen.CombinationDetailScreen
import com.refit.app.ui.screen.CombinationRegisterScreen
import com.refit.app.ui.screen.CreatedCombinationListScreen
import com.refit.app.ui.screen.LikedCombinationListScreen
import com.refit.app.ui.screen.MypageScreen
import com.refit.app.ui.screen.OrderListScreen
import com.refit.app.ui.screen.EditBasicInfoScreen
import com.refit.app.ui.screen.HealthEditScreen
import com.refit.app.ui.screen.ProductSelectScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenWithBottomNav(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash",
    onCartChanged: () -> Unit,
    onLoggedIn: () -> Unit = {},
    onLoggedOut: () -> Unit = {}
)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val bottomTabs = listOf("home", "category", "myfit", "community", "my", "sleepDetail", "stepsDetail", "weatherDetail", "ingredient")
    val noBottomTabs = listOf("myfit/register", "myfit/edit")

    // 스플래시/인증 경로에서는 상단 및 하단 바 숨김 처리
    val hideBars = currentRoute == "splash" || currentRoute.startsWith("auth/login")
    Scaffold(
        topBar = {
            if (!hideBars) {
                Box(Modifier.padding(vertical = 8.dp)) {
                    RefitTopBar(
                        config = appBarFor(
                            route = currentRoute,
                            nav = navController
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (!hideBars &&
                noBottomTabs.none { currentRoute.startsWith(it) } &&
                bottomTabs.any { currentRoute.startsWith(it) }
            ) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Splash
                composable("splash") {
                    SplashScreen(
                        onDecide = { loggedIn ->
                            if (loggedIn) {
                                onLoggedIn()
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                onLoggedOut()
                                navController.navigate("auth/login") {
                                    popUpTo("splash") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
                // Auth
                composable("auth/login") {
                    LoginScreen(
                        onClose = { /* 필요시 */ },
                        onSignup = { navController.navigate("auth/signup") },
                        onLoggedIn = {
                            onLoggedIn()
                            navController.navigate("home") {
                                popUpTo("auth/login") { inclusive = true } // 뒤로가기로 로그인 안 돌아오게
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // 회원가입 네비 그래프
                navigation(
                    startDestination = "auth/signup1",
                    route = "auth/signup"
                ) {
                    composable("auth/signup1") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("auth/signup")
                        }
                        val vm: SignupViewModel = viewModel(parentEntry)

                        // KakaoFlowStore에서 프리필 값 수집
                        val prefillNick by KakaoFlowStore.prefillNickname.collectAsState()
                        val prefillEmail by KakaoFlowStore.prefillEmail.collectAsState()

                        SignupStep1Screen(
                            mode = FormMode.SIGNUP,
                            onBack = { navController.popBackStack() },
                            onNextOrSubmit = { navController.navigate("auth/signup2") },
                            onSearchAddress = { /* 주소검색 다이얼로그 열기 */ },
                            prefillNickname = prefillNick,
                            prefillEmail = prefillEmail,
                            vm = vm
                        )
                    }

                    composable("auth/signup2") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("auth/signup")
                        }
                        val vm: SignupViewModel = viewModel(parentEntry)

                        // KakaoFlowStore에서 토큰/아이디 수집
                        val kakaoToken by KakaoFlowStore.kakaoAccessToken.collectAsState()
                        val kakaoId    by KakaoFlowStore.kakaoId.collectAsState()
                        val kakaoVm: com.refit.app.data.auth.modelAndView.KakaoLoginViewModel = viewModel(parentEntry)

                        SignupStep2Screen(
                            selectedSkinType = vm.uiState.skinType,
                            selectedSkinConcerns = vm.uiState.skinConcerns,
                            selectedScalpConcerns = vm.uiState.scalpConcerns,
                            selectedHealthConcerns = vm.uiState.healthConcerns,
                            onSkinTypeChange = vm::setSkinType,
                            onToggleSkinConcern = vm::toggleSkinConcern,
                            onToggleScalpConcern = vm::toggleScalpConcern,
                            onToggleHealthConcern = vm::toggleHealthConcern,
                            onBack = { navController.popBackStack() },
                            onNextOrSubmit = {
                                val req = vm.buildSignupAllRequest()

                                if (!kakaoToken.isNullOrBlank() && !kakaoId.isNullOrBlank()) {
                                    kakaoVm.signupWithKakao(
                                        kakaoAccessToken = kakaoToken!!,
                                        signupAll = req,
                                        kakaoId = kakaoId!!,
                                        onSuccessLogin = {
                                            val nick = vm.uiState.nickname
                                            val encoded = URLEncoder.encode(nick, StandardCharsets.UTF_8.name())
                                            KakaoFlowStore.clear()
                                            navController.navigate("auth/signup3?nickname=$encoded")
                                        },
                                        onError = { /* TODO: 에러 표시 */ }
                                    )
                                } else {
                                    // 일반 회원가입
                                    vm.submitSignup(
                                        onSuccess = {
                                            val nick = vm.uiState.nickname
                                            val encoded = URLEncoder.encode(nick, StandardCharsets.UTF_8.name())
                                            navController.navigate("auth/signup3?nickname=$encoded")
                                        },
                                        onError = { /* TODO: 에러 표시 */ }
                                    )
                                }
                            },
                            submitEnabled = vm.isStep2Valid && vm.isValid
                        )
                    }

                    composable("account/edit") {
                        EditBasicInfoScreen(
                            onBack = { navController.popBackStack() },
                            onSaved = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("account/health/edit") {
                        HealthEditScreen(
                            onBack = { navController.popBackStack() },
                            onSaved = {
                                // 저장 성공 알림(임시) → 마이페이지로
                                navController.navigate("my") {
                                    popUpTo("account/health/edit") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(
                        route = "auth/signup3?nickname={nickname}",
                        arguments = listOf(navArgument("nickname") { defaultValue = "" })
                    ) { backStackEntry ->
                        val nickname = backStackEntry.arguments?.getString("nickname").orEmpty()

                        SignupStep3Screen(
                            nickname = nickname,
                            onBack = { navController.popBackStack() },
                            onLogin = {
                                navController.navigate("auth/login") {
                                    popUpTo("auth/signup") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
                // 기본 탭
                composable("home") { HomeScreen(navController) }
                composable("category") { CategoryScreen(navController) }
                composable("myfit") { MyfitScreen(navController = navController) }
                composable("community") { CommunityScreen(navController) }
                composable("my") {
                    MypageScreen(
                        navController = navController,
                        onCartChanged = onCartChanged
                    )
                }

                // 성분 분석 (부모 라우트에 VM 스코프 고정)
                composable("ingredient") { backStackEntry ->
                    val app = LocalContext.current.applicationContext as Application

                    val parentEntry = remember(backStackEntry) { backStackEntry }
                    val vm: AnalysisViewModel =
                        viewModel(parentEntry, factory = AnalysisViewModelFactory(app))

                    AnalysisScreen(navController = navController, vm = vm)
                }

                composable("ingredient/result") { backStackEntry ->
                    val app = LocalContext.current.applicationContext as Application
                    val parentEntry = remember(backStackEntry) {
                        // 아래 라우트가 백스택에 남아 있으므로 이 엔트리를 통해 같은 VM 인스턴스를 재사용
                        navController.getBackStackEntry("ingredient")
                    }
                    val vm: AnalysisViewModel =
                        viewModel(parentEntry, factory = AnalysisViewModelFactory(app))

                    AnalysisResultScreen(ui = vm.ui.value)
                }

                // 검색/알림/장바구니
                composable("notifications") { NotificationScreen() }
                composable("cart") {
                    CartScreen(
                        navController = navController,
                        onCartChanged = onCartChanged
                    )
                }
                composable(
                    route = "search?query={query}",
                    arguments = listOf(navArgument("query") {
                        nullable = true
                        defaultValue = null
                    })
                ) {
                    SearchScreen(navController)
                }

                // 상품 상세 페이지
                composable(
                    route = "product/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments!!.getInt("id")
                    ProductDetailScreen(
                        productId = id,
                        navController = navController,
                        onCartChanged = onCartChanged
                    )
                }

                // 찜 목록
                composable("wish") { WishScreen(navController) }

                // home에서의 걸음/수면/날씨 버튼 눌렀을 경우 상세
                composable("stepsDetail") { StepsDetailScreen(navController) }
                composable("sleepDetail") { SleepDetailScreen(navController) }
                composable("weatherDetail") { WeatherDetailScreen(navController) }

                // 맞춤형 추천 상품 목록
                composable(
                    "recommendation/{type}",
                    arguments = listOf(navArgument("type") { type = NavType.IntType })
                ) { backStackEntry ->
                    val type = backStackEntry.arguments?.getInt("type") ?: 0
                    RecommendationScreen(navController, type)
                }

                // 마이핏 - 제품 등록/수정
                composable("myfit/register") { MyfitRegisterScreen(navController) }
                composable(
                    route = "myfit/edit/{memberProductId}",
                    arguments = listOf(navArgument("memberProductId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments!!.getLong("memberProductId")

                    // "myfit" 화면과 같은 스코프의 VM을 가져와 목록을 재사용
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("myfit")
                    }
                    val myfitVm: MyfitViewModel = viewModel(parentEntry)
                    val ui = myfitVm.ui

                    // 목록에서 편집 대상 찾기 (using/completed 중 현재 보이는 리스트)
                    val item = remember(ui.items, id) {
                        ui.items.firstOrNull { it.memberProductId == id }
                    }

                    if (item != null) {
                        MyfitEditScreen(
                            item = item,
                            navController = navController
                        )
                    } else {
                        // 목록에 대상이 없을 때의 처리 (간단한 플레이스홀더)
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("편집할 항목을 찾을 수 없습니다.")
                        }
                    }
                }

                // 내가 저장한 조합 목록
                composable("liked_combinations") { LikedCombinationListScreen(navController) }

                // 내 주문 내역
                composable("orders") {
                    OrderListScreen(
                        navController = navController,
                        onCartChanged = onCartChanged
                    )
                }

                // 내가 생성한 조합 목록
                composable("created_combinations") { CreatedCombinationListScreen(navController) }

                // 조합 상세 페이지
                composable(
                    route = "combinationDetail/{combinationId}",
                    arguments = listOf(navArgument("combinationId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val combinationId = backStackEntry.arguments?.getLong("combinationId") ?: return@composable
                    CombinationDetailScreen(
                        navController = navController,
                        combinationId = combinationId,
                        onCartChanged = onCartChanged
                    )
                }

                // 조합 등록 페이지
                composable("combinationRegister") { backStackEntry ->
                    val selectedProducts =
                        backStackEntry.savedStateHandle
                            .getStateFlow("selectedProducts", emptyList<Product>())
                            .collectAsState().value

                    CombinationRegisterScreen(
                        navController = navController,
                        selectedProducts = selectedProducts,
                        onSearchClick = { bh ->
                            navController.navigate("productSelect/$bh")
                        },
                        onRegisterSuccess = {
                            navController.popBackStack()
                        }
                    )
                }

                // 조합 등록 내 검색페이지
                composable(
                    route = "productSelect/{bhType}",
                    arguments = listOf(navArgument("bhType") { nullable = true })
                ) { backStackEntry ->
                    val bhType = backStackEntry.arguments?.getString("bhType")

                    ProductSelectScreen(
                        navController = navController,
                        bhType = bhType,
                        onConfirm = { selectedProducts ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selectedProducts", selectedProducts)
                            navController.popBackStack()
                        }
                    )
                }

                composable("created_combinations") { CreatedCombinationListScreen(navController) }

                // 채팅방
                composable(
                    route = "chat/{categoryId}",
                    arguments = listOf(
                        navArgument("categoryId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments!!.getLong("categoryId")
                    ChatRoomScreen(
                        navController = navController,
                        categoryId    = categoryId
                    )
                }
            }
        }
    }
}