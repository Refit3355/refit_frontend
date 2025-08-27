package com.refit.app.data.push.repository

import com.refit.app.data.push.api.NotificationApi
import com.refit.app.data.push.model.BadgeResponse
import com.refit.app.data.push.model.BaseResponse
import com.refit.app.data.push.model.NotificationListResponse
import com.refit.app.data.push.model.RegisterDeviceRequest
import com.refit.app.network.RetrofitInstance

class NotificationRepository(
    private val api: NotificationApi = RetrofitInstance.create(NotificationApi::class.java)
) {
    suspend fun registerDevice(platform: String, fcmToken: String, deviceId: String) {
        api.registerDevice(RegisterDeviceRequest(platform, fcmToken, deviceId))
        // 서버에서 BaseResponse/204 둘 중 하나여도 예외만 아니면 성공으로 간주
    }

    suspend fun deleteByDeviceId(deviceId: String) {
        api.deleteByDeviceId(deviceId)
    }

    suspend fun getBadge(): BadgeResponse = api.getBadge()

    suspend fun readAll(): BaseResponse = api.readAll()

    suspend fun getNotifications(offset: Int = 0, size: Int = 20): NotificationListResponse =
        api.getNotifications(offset, size)
}
