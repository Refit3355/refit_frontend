package com.refit.app.ui.composable.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.refit.app.ui.screen.CartScreen
import com.refit.app.ui.screen.CategoryScreen
import com.refit.app.ui.screen.CommunityScreen
import com.refit.app.ui.screen.HomeScreen
import com.refit.app.ui.screen.LoginScreen
import com.refit.app.ui.screen.MyScreen
import com.refit.app.ui.screen.MyfitScreen
import com.refit.app.ui.screen.NotificationScreen
import com.refit.app.ui.screen.ProductDetailScreen
import com.refit.app.ui.screen.SearchScreen
import com.refit.app.ui.screen.SignupStep1Screen
import com.refit.app.ui.screen.SignupStep2Screen
import com.refit.app.ui.screen.SignupStep3Screen
import com.refit.app.ui.screen.SplashScreen
import com.refit.app.ui.screen.WishScreen
import com.refit.app.ui.viewmodel.auth.SignupViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenWithBottomNav(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash"
)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination

    val bottomTabs = listOf("home", "category", "myfit", "community", "my")

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
                        onSignup = { navController.navigate("auth/signup1") }
                    )
                }
                composable("auth/signup1") {
                    SignupStep1Screen(
                        onBack = { navController.popBackStack() },
                        onNextOrSubmit = { navController.navigate("auth/signup2") },
                        onSearchAddress = { /* 주소 검색 */ }
                    )
                }
                composable("auth/signup2") {
                    val vm: SignupViewModel = viewModel()
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
                        onNextOrSubmit = { navController.navigate("auth/signup3") },
                        submitEnabled = vm.isStep2Valid
                    )
                }

                composable("auth/signup3") {
                    SignupStep3Screen(
                        nickname = null, // SharedPreferences에서 닉네임 꺼내서 넘기면 됨
                        onBack = { navController.popBackStack() },
                        onLogin = {
                            navController.navigate("home") {
                                popUpTo("auth/login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                // 기본 탭
                composable("home") { HomeScreen(navController) }
                composable("category") { CategoryScreen(navController) }
                composable("myfit") { MyfitScreen(navController) }
                composable("community") { CommunityScreen(navController) }
                composable("my") { MyScreen(navController) }

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
                // 개발중 : 삼성헬스 데이터 확인용 스크린
                composable("health_dev") { com.refit.app.ui.screen.HealthScreen() }

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
            }
        }
    }
}