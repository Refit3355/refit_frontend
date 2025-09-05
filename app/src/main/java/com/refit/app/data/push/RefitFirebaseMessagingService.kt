package com.refit.app.data.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.refit.app.R
import com.refit.app.data.push.bus.PushEvents
import com.refit.app.data.push.model.NotificationBus
import com.refit.app.data.push.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class RefitFirebaseMessagingService : FirebaseMessagingService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val repo = NotificationRepository()

    override fun onNewToken(token: String) {
        Log.d("FCM", "new token: $token")

        scope.launch {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                ?: "unknown"

            runCatching {
                repo.registerDevice(
                    platform = "ANDROID",
                    fcmToken = token,
                    deviceId = deviceId
                )
            }.onFailure {
                Log.w("FCM", "register token failed", it)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        val unread = msg.data["unreadCount"]?.toIntOrNull()
        val d = msg.data
        val title = d["title"] ?: msg.notification?.title ?: "알림"
        val body = d["body"] ?: msg.notification?.body ?: ""
        val deeplink = d["deeplink"] ?: "app://notification"
        if (unread != null) {
            PushEvents.tryEmitBadge(unread)
        } else {
            NotificationBus.onNew()
        }
        showSystemNotification(title, body, deeplink)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showSystemNotification(title: String, body: String, deeplink: String) {
        val ctx = applicationContext
        val channelId = "refit_default"
        ensureChannel(ctx, channelId)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
            setPackage(ctx.packageName)
        }
        val pi = PendingIntent.getActivity(
            ctx, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // largeIcon (컬러 비트맵)
        val largeIconBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_launcher)

        val builder = NotificationCompat.Builder(ctx, channelId)
            // smallIcon: 단색 심볼 (상태바/알림바용)
            .setSmallIcon(R.drawable.ic_noti_small)
            // largeIcon: 알림 카드 안쪽 큰 로고
            .setLargeIcon(largeIconBitmap)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(ctx).notify(Random.nextInt(), builder.build())
    }

    private fun ensureChannel(ctx: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "일반 알림", NotificationManager.IMPORTANCE_HIGH)
            ctx.getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
    }

    companion object {
        suspend fun currentToken(): String = FirebaseMessaging.getInstance().token.await()
    }
}
