package com.refit.app.ui.composable.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.refit.app.ui.screen.HealthScreen
import com.refit.app.ui.screen.HomeScreen
import com.refit.app.ui.screen.MyScreen
import com.refit.app.ui.screen.MyfitEditScreen
import com.refit.app.ui.screen.MyfitRegisterScreen
import com.refit.app.ui.screen.MyfitScreen
import com.refit.app.ui.screen.NotificationScreen
import com.refit.app.ui.screen.ProductDetailScreen
import com.refit.app.ui.screen.SearchScreen
import com.refit.app.ui.screen.WishScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.refit.app.data.myfit.viewmodel.MyfitViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenWithBottomNav(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "home"
)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val bottomTabs = listOf("home", "category", "myfit", "community", "my")
    val noBottomTabs = listOf("myfit/register", "myfit/edit")

    Scaffold(
        topBar = {
            Box(Modifier.padding(vertical = 8.dp)) {
                RefitTopBar(
                    config = appBarFor(
                        route = currentRoute,
                        nav = navController
                    )
                )
            }
        },
        bottomBar = {
            if (noBottomTabs.none { currentRoute.startsWith(it) } &&
                bottomTabs.any { currentRoute.startsWith(it) }) {
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
                // 기본 탭
                composable("home") { HomeScreen(navController) }
                composable("category") { CategoryScreen(navController) }
                composable("myfit") { MyfitScreen(navController = navController) }
                composable("community") { CommunityScreen(navController) }
                composable("my") { MyScreen(navController) }

                // 검색/알림/장바구니
                composable("notifications") { NotificationScreen(navController) }
                composable("cart") { CartScreen(navController) }
                composable("search") { SearchScreen(navController) }

                // 개발중 : 삼성헬스 데이터 확인용 스크린
                composable("health_dev") { HealthScreen() }

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

            }
        }
    }
}