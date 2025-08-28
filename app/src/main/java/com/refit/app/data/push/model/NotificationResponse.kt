package com.refit.app.data.push.model

data class RegisterDeviceRequest(
    val platform: String = "android",
    val fcmToken: String,
    val deviceId: String? = null
)

data class BaseResponse(val message: String? = null)
data class BadgeResponse(val unreadCount: Int)

data class NotificationListResponse(
    val items: List<NotificationRow>,
    val size: Int
)

data class NotificationRow(
    val notificationId: Long,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val deeplink: String?,
    val type: String,
    val isRead: Int,
    val createdAt: String?,
    val readAt: String?
)
