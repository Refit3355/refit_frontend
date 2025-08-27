package com.refit.app.data.push

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.refit.app.data.push.repository.NotificationRepository
import kotlinx.coroutines.tasks.await

object PushRegistrar {

    private fun deviceId(ctx: Context): String =
        Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"

    private fun platform(): String = "ANDROID"

    // 로그인 직후 혹은 앱 재실행 시(로그인 상태라면)
    suspend fun register(context: Context, repo: NotificationRepository) {
        runCatching {
            val token = FirebaseMessaging.getInstance().token.await()
            repo.registerDevice(
                platform = platform(),
                fcmToken = token,
                deviceId = deviceId(context)
            )
        }.onFailure { Log.w("FCM", "registerDevice failed", it) }
    }

    // 로그아웃 직전에 호출
    suspend fun unregister(context: Context, repo: NotificationRepository) {
        val devId = deviceId(context)

        // 1) 서버에서 (memberId, deviceId) 매핑 삭제
        runCatching { repo.deleteByDeviceId(devId) }
            .onFailure { Log.w("FCM", "deleteByDeviceId API failed", it) }

        // 2) 로컬 FCM 토큰 폐기 → 다음 로그인 시 새 토큰
        runCatching { FirebaseMessaging.getInstance().deleteToken().await() }
            .onFailure { Log.w("FCM", "Firebase deleteToken() failed", it) }
    }
}
