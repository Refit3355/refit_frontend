package com.refit.app.data.myfit.repository

import com.refit.app.data.myfit.api.MyfitApi
import com.refit.app.data.myfit.model.CreateMemberProductRequest
import com.refit.app.data.myfit.model.MemberProductListResponse
import com.refit.app.data.myfit.model.PurchasedProductResponse
import com.refit.app.data.myfit.model.UpdateMemberProductRequest
import com.refit.app.network.RetrofitInstance
import retrofit2.HttpException

class MyfitRepository(
    private val api: MyfitApi = RetrofitInstance.create(MyfitApi::class.java)
) {
    suspend fun getMemberProducts(type: String, status: String): MemberProductListResponse {
        return api.getMemberProducts(type, status)
    }

    suspend fun completeMemberProduct(memberProductId: Long) {
        val res = api.updateMemberProductStatus(memberProductId, "completed")
        if (!res.isSuccessful) throw HttpException(res)
    }

    suspend fun deleteMemberProduct(memberProductId: Long) {
        val res = api.deleteMemberProduct(memberProductId)
        if (!res.isSuccessful) throw HttpException(res)
    }

    suspend fun updateMemberProduct(
        memberProductId: Long,
        req: UpdateMemberProductRequest
    ) = api.updateMemberProduct(memberProductId, req)

    suspend fun createCustom(req: CreateMemberProductRequest) {
        val res = api.createCustomMemberProduct(req)
        if (!res.isSuccessful) throw HttpException(res)
    }

    suspend fun getPurchasedProducts(type: String): PurchasedProductResponse =
        api.getPurchasedProducts(type)

    suspend fun createFromOrderItem(orderItemId: Long) {
        val res = api.createFromOrderItem(orderItemId)
        if (!res.isSuccessful) {
            throw HttpException(res)
        }
    }
}
