package com.refit.app.data.push.api

import com.refit.app.data.push.model.BadgeResponse
import com.refit.app.data.push.model.BaseResponse
import com.refit.app.data.push.model.NotificationListResponse
import com.refit.app.data.push.model.RegisterDeviceRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {
    @POST("/notifications/devices/register")
    @Headers("Requires-Auth: true")
    suspend fun registerDevice(@Body body: RegisterDeviceRequest): BaseResponse

    @DELETE("/notifications/devices/token")
    @Headers("Requires-Auth: true")
    suspend fun deleteByDeviceId(@Query("deviceId") deviceId: String): BaseResponse

    @GET("/notifications/badge")
    @Headers("Requires-Auth: true")
    suspend fun getBadge(): BadgeResponse

    @POST("/notifications/read-all")
    @Headers("Requires-Auth: true")
    suspend fun readAll(): BaseResponse

    @GET("/notifications")
    @Headers("Requires-Auth: true")
    suspend fun getNotifications(
        @Query("offset") offset: Int = 0,
        @Query("size") size: Int = 20
    ): NotificationListResponse
}