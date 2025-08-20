package com.refit.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.refit.app.ui.composable.common.MainScreenWithBottomNav
import com.refit.app.ui.theme.RefitTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartBadgeViewModel
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.data.health.HealthRepo
import com.refit.app.data.local.cart.LocalCartCount
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        RetrofitInstance.init(this)

        // 개발시 토큰 넣기
        //TokenManager.saveToken("")

        enableEdgeToEdge()

        setContent {
            RefitTheme {
                val navController = rememberNavController()

                // 앱 시작 시 Health Connect 권한 자동 요청
//                RequestHealthPermissionsOnStart()

                // 전역 제공용 Local
                val cartApi = remember { RetrofitInstance.create(CartApi::class.java) }
                val repo    = remember { CartRepository(cartApi) }
                val vm      = remember { CartBadgeViewModel(repo) }

                val count by vm.badgeCount.collectAsStateWithLifecycle(0)

                // 알림 클릭 여부 판단
                val navigateTo = intent?.getStringExtra("navigateTo")

                // 항상 홈으로 시작
                CompositionLocalProvider(LocalCartCount provides count) {
                    MainScreenWithBottomNav(
                        navController = navController,
                        startDestination = "home",
                        onCartChanged = { vm.refreshCount() }
                    )
                }

                // 알림 클릭 시, NavHost 초기화 후 알림화면으로 이동
                LaunchedEffect(Unit) {
                    if (!TokenManager.getToken().isNullOrBlank()) {
                        vm.refreshCount()
                    }
                    if (navigateTo == "notifications") {
                        navController.navigate("notifications")
                    }
                }
            }
        }
    }
}

// Health Connect 권한 요청 전용 컴포저블
@Composable
private fun RequestHealthPermissionsOnStart() {
    val ctx = LocalContext.current

    // Health Connect 권한 요청 런처
    val launcher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { /* result: Set<HealthPermission> 이므로 별도 처리 불필요 */ }

    // 컴포지션 시 1회 체크 → 미승인 시 즉시 요청
    LaunchedEffect(Unit) {
        val client = HealthRepo.client(ctx)
        val granted = client.permissionController.getGrantedPermissions()
        if (!granted.containsAll(HealthRepo.readPerms)) {
            launcher.launch(HealthRepo.readPerms)
        }
    }
}
