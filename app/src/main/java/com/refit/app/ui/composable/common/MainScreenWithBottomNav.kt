package com.refit.app.ui.composable.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.refit.app.ui.screen.MypageScreen
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
import com.refit.app.data.auth.modelAndView.SignupViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenWithBottomNav(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash"
)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination

    val bottomTabs = listOf("home", "category", "myfit", "community", "my", "sleepDetail", "stepsDetail", "weatherDetail")

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
            if (!hideBars && bottomTabs.any { currentRoute.startsWith(it) }) {
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
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
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

                        SignupStep1Screen(
                            onBack = { navController.popBackStack() },
                            onNextOrSubmit = { navController.navigate("auth/signup2") },
                            onSearchAddress = { /* 주소 검색 */ },
                            vm = vm
                        )
                    }

                    composable("auth/signup2") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("auth/signup")
                        }
                        val vm: SignupViewModel = viewModel(parentEntry)

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
                                // 가입 API 호출
                                vm.submitSignup(
                                    onSuccess = { _ ->
                                        val nick = vm.uiState.nickname
                                        val encoded = URLEncoder.encode(nick, StandardCharsets.UTF_8.name())
                                        navController.navigate("auth/signup3?nickname=$encoded") {
                                        }
                                    },
                                    onError = { msg ->
                                        // TODO: 스낵바/토스트 등으로 msg 표시
                                    }
                                )
                            },
                            // 입력 전체(valid) + step2 선택(skinType) 둘 다 만족해야 버튼 활성화
                            submitEnabled = vm.isStep2Valid && vm.isValid
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
                composable("myfit") { MyfitScreen(navController) }
                composable("community") { CommunityScreen(navController) }
                composable("my") { MypageScreen(navController) }

                // 검색/알림/장바구니
                composable("notifications") { NotificationScreen(navController) }
                composable("cart") { CartScreen(navController) }
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
                    ProductDetailScreen(productId = id, navController = navController)
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
            }
        }
    }
}