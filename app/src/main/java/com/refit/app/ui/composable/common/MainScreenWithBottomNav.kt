package com.refit.app.ui.composable.common

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.navDeepLink
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.refit.app.data.order.flow.CheckoutFlowStore
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.network.RetrofitInstance
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
import com.refit.app.BuildConfig
import com.refit.app.ui.screen.AnalysisScreen
import com.refit.app.ui.screen.CombinationDetailScreen
import com.refit.app.ui.screen.CreatedCombinationListScreen
import com.refit.app.ui.screen.LikedCombinationListScreen
import com.refit.app.ui.screen.MypageScreen
import com.refit.app.ui.screen.OrderListScreen
import com.refit.app.ui.screen.EditBasicInfoScreen
import com.refit.app.ui.screen.HealthEditScreen
import com.refit.app.ui.screen.SignupFlowScreen
import com.refit.app.data.auth.modelAndView.FormMode
import com.refit.app.data.auth.modelAndView.KakaoFlowStore
import com.refit.app.ui.screen.AnalysisScreen
import com.refit.app.ui.screen.CombinationDetailScreen
import com.refit.app.ui.screen.CreatedCombinationListScreen
import com.refit.app.ui.screen.LikedCombinationListScreen
import com.refit.app.ui.screen.MypageScreen
import com.refit.app.ui.screen.OrderListScreen
import com.refit.app.ui.screen.EditBasicInfoScreen
import com.refit.app.ui.screen.HealthEditScreen
import com.refit.app.ui.screen.SignupFlowScreen
import com.refit.app.data.order.model.decodeDraftOrderRequest
import com.refit.app.ui.screen.order.OrderSheetScreen
import com.refit.app.ui.screen.order.PayFailScreen
import com.refit.app.ui.screen.order.TossWebViewScreen
import com.refit.app.data.myfit.viewmodel.MyfitViewModel
import com.refit.app.data.auth.modelAndView.SignupViewModel
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

    val bottomTabs = listOf("home", "category", "myfit", "community", "my", "sleepDetail", "stepsDetail", "weatherDetail")
    val noBottomTabs = listOf("myfit/register", "myfit/edit", "checkout/")

    // 스플래시/인증 경로에서는 상단 및 하단 바 숨김 처리
    val hideBars = currentRoute == "splash" || currentRoute.startsWith("auth/")
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
                composable("my") { MypageScreen(navController) }

                // 성분 분석
                composable("ingredient") { AnalysisScreen() }

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
                composable("liked_combinations") { LikedCombinationListScreen() }

                // 내 주문 내역
                composable("orders") { OrderListScreen(navController) }

                // 내가 생성한 조합 목록
                composable("created_combinations") { CreatedCombinationListScreen() }

                // 조합 상세 페이지
                composable(
                    route = "combinationDetail/{combinationId}",
                    arguments = listOf(navArgument("combinationId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val combinationId = backStackEntry.arguments?.getLong("combinationId") ?: return@composable
                    CombinationDetailScreen(
                        navController = navController,
                        combinationId = combinationId
                    )
                }


                // 문자열 인코딩 유틸
                fun enc(s: String) = java.net.URLEncoder.encode(s, "utf-8")

                navigation(
                    startDestination = NavRoutes.OrderSheet,
                    route = NavRoutes.CheckoutRoot
                ) {
                    // 주문서 화면
                    composable(
                        route = NavRoutes.OrderSheet,
                        arguments = listOf(
                            navArgument("payload") {
                                type = NavType.StringType
                            } // DraftOrderRequest 를 JSON+URL-encode로 전달
                        )
                    ) { back ->
                        // payload를 복원해서 DraftOrderRequest 로 파싱
                        val payload = back.arguments?.getString("payload").orEmpty()
                        val draftReq = decodeDraftOrderRequest(payload)

                        OrderSheetScreen(
                            navController = navController,
                            draftReq = draftReq,
                            clientKey = BuildConfig.TOSS_CLIENT_KEY,
                            successUrl = "refitapp://pay/success",
                            failUrl = "refitapp://pay/fail"
                        )
                    }

                    composable(
                        route =
                            "tossPay?orderId={orderId}" +
                                    "&orderName={orderName}" +
                                    "&amount={amount}" +
                                    "&method={method}" +
                                    "&successUrl={successUrl}" +
                                    "&failUrl={failUrl}",
                        arguments = listOf(
                            navArgument("orderId")   { type = NavType.StringType },  // "ORD-..." ← String
                            navArgument("orderName") { type = NavType.StringType },
                            navArgument("amount")    { type = NavType.LongType },
                            navArgument("method")    { type = NavType.StringType },
                            navArgument("successUrl"){ type = NavType.StringType },
                            navArgument("failUrl")   { type = NavType.StringType },
                        )
                    ) { backStackEntry ->
                        val args      = backStackEntry.arguments!!
                        val orderId   = args.getString("orderId")!!
                        val orderName = args.getString("orderName")!!
                        val amount    = args.getLong("amount")
                        val method    = args.getString("method")!!
                        val success   = Uri.decode(args.getString("successUrl") ?: "")
                        val fail      = Uri.decode(args.getString("failUrl") ?: "")

                        Log.d("TOSS", BuildConfig.TOSS_CLIENT_KEY.take(8))
                        TossWebViewScreen(
                            navController = navController,
                            orderId = orderId,
                            orderName = orderName,
                            amount = amount,
                            method = method,
                            clientKey = BuildConfig.TOSS_CLIENT_KEY,
                            successUrl = success,
                            failUrl = fail
                        )
                    }

                    composable(
                        route = "checkout/payResult?paymentKey={paymentKey}&orderId={orderId}&amount={amount}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "refitapp://pay/success?paymentKey={paymentKey}&orderId={orderId}&amount={amount}"
                            }
                        ),
                        arguments = listOf(
                            navArgument("paymentKey") { type = NavType.StringType },
                            navArgument("orderId")    { type = NavType.StringType },
                            navArgument("amount")     { type = NavType.LongType }
                        )
                    ) { back ->
                        val paymentKey = back.arguments!!.getString("paymentKey")!!
                        val orderId    = back.arguments!!.getString("orderId")!!
                        val amount     = back.arguments!!.getLong("amount")

                        val cartApi  = remember { RetrofitInstance.create(CartApi::class.java) }
                        val cartRepo = remember { CartRepository(cartApi) }
                        val scope    = rememberCoroutineScope()

                        com.refit.app.ui.composable.order.PayResultHandler(
                            navController = navController,
                            paymentKey = paymentKey,
                            orderId = orderId,
                            amount = amount,
                            onSuccessNavigate = { orderPk ->
                                // 선택했던 장바구니 항목들 삭제
                                scope.launch {
                                    val ids = CheckoutFlowStore.selectedCartIds.value
                                    if (ids.isNotEmpty()) {
                                        // 서버가 이미 정리하는 경우도 있으니 에러는 무시
                                        runCatching { cartRepo.deleteBulk(ids) }
                                        CheckoutFlowStore.clear()
                                        // 뱃지/목록 갱신 콜백
                                        onCartChanged()
                                    }
                                }

                                // 성공 후 이동 로직. orderPk가 없으면 마이페이지로 보내는 등 정책 결정
                                if (orderPk > 0) {
                                    navController.navigate("orders") {
                                        popUpTo(NavRoutes.CheckoutRoot) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("my") {
                                        popUpTo(NavRoutes.CheckoutRoot) { inclusive = true }
                                    }
                                }
                            },
                            onFailNavigate = {
                                navController.navigate("cart") {
                                    popUpTo(NavRoutes.CheckoutRoot) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        route = "checkout/payFail?code={code}&message={message}",
                        arguments = listOf(
                            navArgument("code")    { type = NavType.StringType; nullable = true; defaultValue = null },
                            navArgument("message") { type = NavType.StringType; nullable = true; defaultValue = null },
                        )
                    ) { backStackEntry ->
                        val code = backStackEntry.arguments?.getString("code") ?: "UNKNOWN"
                        val message = backStackEntry.arguments?.getString("message") ?: ""
                        PayFailScreen(navController = navController, code = code, message = message)
                    }
                }
            }
        }
    }
}