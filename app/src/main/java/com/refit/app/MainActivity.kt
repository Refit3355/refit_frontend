package com.refit.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.refit.app.ui.composable.common.MainScreenWithBottomNav
import com.refit.app.ui.theme.RefitTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kakao.sdk.common.KakaoSdk
import com.refit.app.data.cart.api.CartApi
import com.refit.app.data.cart.modelAndView.CartBadgeViewModel
import com.refit.app.data.cart.repository.CartRepository
import com.refit.app.data.health.HealthRepo
import com.refit.app.data.local.cart.LocalCartCount
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import android.Manifest
import android.content.Intent
import androidx.compose.runtime.rememberCoroutineScope
import com.google.firebase.messaging.FirebaseMessaging
import com.refit.app.data.push.repository.NotificationRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.provider.Settings
import com.refit.app.data.push.PushRegistrar

class MainActivity : ComponentActivity() {

    private var deeplinkHandler: ((Uri) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        RetrofitInstance.init(this)
        UserPrefs.init(this) // 사용자 정보 prefs
        PushChannels.ensure(this)

        Log.d("kakaoApp", BuildConfig.KAKAO_NATIVE_APP_KEY)

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        enableEdgeToEdge()

        setContent {
            RefitTheme {
                val navController = rememberNavController()

                //  Compose 안에서 Activity 필드 콜백을 설정
                deeplinkHandler = { uri -> handleDeeplink(navController, uri) }

                // Health Connect 권한 자동 요청
                RequestHealthPermissionsOnStart()

                // 위치 권한 자동 요청
                RequestLocationPermissionsOnStart()

                // 푸시 알림 자동 요청
                RequestPostNotificationsOnStart()

                // 전역 제공용 Local
                val cartApi = remember { RetrofitInstance.create(CartApi::class.java) }
                val repo    = remember { CartRepository(cartApi) }
                val vm      = remember { CartBadgeViewModel(repo) }

                val count by vm.badgeCount.collectAsStateWithLifecycle(0)

                // 푸시 등록/삭제 준비
                val pushRepo = remember { NotificationRepository() }
                val scope = rememberCoroutineScope()
                val appCtx = applicationContext

                // 알림 클릭 여부 판단
                val navigateTo = intent?.getStringExtra("navigateTo")

                // 항상 스플래시로 시작
                CompositionLocalProvider(LocalCartCount provides count) {
                    MainScreenWithBottomNav(
                        navController = navController,
                        startDestination = "splash",
                        onCartChanged = { vm.refreshCount() },      // 어디서든 수량 변경 시 갱신
                        onLoggedIn    = {
                            vm.refreshCount()  // 로그인 직후 안전 재갱신
                            scope.launch { PushRegistrar.register(appCtx, pushRepo) }
                        },
                        onLoggedOut   = {
                            vm.clearCount()  // 로그아웃 시 0으로 초기화
                            scope.launch { PushRegistrar.unregister(appCtx, pushRepo) }
                        }
                    )
                }

                // 알림 클릭 시, NavHost 초기화 후 알림화면으로 이동
                LaunchedEffect(Unit) {
                    if (!TokenManager.getToken().isNullOrBlank()) {
                        vm.refreshCount()
                        scope.launch { PushRegistrar.register(appCtx, pushRepo) }
                    }
                    // 알림 클릭으로 들어온 경우
                    if (navigateTo == "notifications") {
                        navController.navigate("notifications")
                    }
                    // 실제 app://... 딥링크
                    intent?.data?.let { uri ->
                        deeplinkHandler?.invoke(uri)
                        // 재처리 방지
                        intent?.data = null
                    }
                }
            }
        }
    }

    //  액티비티가 살아있는 상태에서(싱글탑 재사용 등) 새 인텐트가 온 경우도 처리
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Compose 쪽에서 다시 읽을 수 있게
        intent?.data?.let { uri ->
            deeplinkHandler?.invoke(uri)
            intent.data = null
        }
    }


    //  딥링크 → NavController 라우팅
    private fun handleDeeplink(nav: androidx.navigation.NavController, uri: Uri) {
        // Toss 결제 콜백 처리
        if (uri.scheme == "refitapp" && uri.host == "pay") {
            when (uri.lastPathSegment) {
                "success" -> {
                    val paymentKey = uri.getQueryParameter("paymentKey").orEmpty()
                    val ordId      = uri.getQueryParameter("orderId").orEmpty()
                    val amt        = uri.getQueryParameter("amount")?.toLongOrNull() ?: 0L
                    android.util.Log.i("DeepLink", "PAY SUCCESS paymentKey=$paymentKey, orderId=$ordId, amount=$amt")
                    nav.navigate(
                        "checkout/payResult?paymentKey=${Uri.encode(paymentKey)}&orderId=${Uri.encode(ordId)}&amount=$amt"
                    )
                }
                "fail" -> {
                    val code = uri.getQueryParameter("code") ?: "UNKNOWN"
                    val msg  = uri.getQueryParameter("message") ?: "결제에 실패했어요"
                    android.util.Log.w("DeepLink", "PAY FAIL code=$code, message=$msg")
                    nav.navigate(
                        "checkout/payFail?code=${Uri.encode(code)}&message=${Uri.encode(msg)}"
                    )
                }
            }
            return
        }

        // app://... 라우팅 유지
        when (uri.host) {
            "myfit"   -> nav.navigate("myfit")
            "orders"  -> {
                val id = uri.pathSegments.firstOrNull()?.toLongOrNull()
                if (id != null) {
                    // nav.navigate("orders/$id")
                    nav.navigate("orders")
                } else {
                    nav.navigate("orders")
                }
            }
            "product" -> {
                val id = uri.pathSegments.firstOrNull()?.toIntOrNull()
                if (id != null) nav.navigate("product/$id")
            }
            else -> { /* 필요 시 기본 라우트 */ }
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

@Composable
fun RequestLocationPermissionsOnStart() {
    val ctx = LocalContext.current

    // 위치 권한 런처
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fineGranted = perms[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = perms[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            // 권한 승인 → 위치 가져오기 가능
        } else {
            // 거부시
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                ctx,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}

// 안드13+에서 POST_NOTIFICATIONS 권한을 1회 요청
@Composable
private fun RequestPostNotificationsOnStart() {
    if (Build.VERSION.SDK_INT < 33) return
    val ctx = androidx.compose.ui.platform.LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not — 별도 처리 필요 시 여기에 */ }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

// 알림 채널 생성(안드8+)
object PushChannels {
    const val MAIN = "refit_main"

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = mgr.getNotificationChannel(MAIN)
            if (existing == null) {
                val ch = NotificationChannel(
                    MAIN, "Re:fit 알림", NotificationManager.IMPORTANCE_HIGH
                )
                mgr.createNotificationChannel(ch)
            }
        }
    }
}