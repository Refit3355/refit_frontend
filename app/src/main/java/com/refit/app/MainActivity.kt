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

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            RefitTheme {
                val navController = rememberNavController()

                // 알림 클릭 여부 판단
                val navigateTo = intent?.getStringExtra("navigateTo")

                // 항상 홈으로 시작
                MainScreenWithBottomNav(
                    navController = navController,
                    startDestination = "home"
                )

                // 알림 클릭 시, NavHost 초기화 후 알림화면으로 이동
                LaunchedEffect(Unit) {
                    if (navigateTo == "notifications") {
                        navController.navigate("notifications")
                    }
                }
            }
        }
    }

}