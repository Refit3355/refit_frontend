package com.refit.app.ui.composable.combiking

data class CombinationDto(
    val combinationId: Long,
    val combinationName: String,
    val price: Int,
    val originalPrice: Int,
    val category: String,
    val likes: Int,
    val images: List<String>,
    val profileUrl: String,
    val nickname: String
)

private const val dummyImageUrl = "https://dimg.donga.com/wps/NEWS/IMAGE/2020/03/03/99990653.1.jpg"

val dummyCombinations = listOf(
    CombinationDto(
        combinationId = 1,
        combinationName = "꿀피부 만드는 조합",
        price = 175000,
        originalPrice = 236000,
        category = "워시&크리",
        likes = 11,
        images = List(5) { dummyImageUrl },
        profileUrl = dummyImageUrl,
        nickname = "외식고기"
    ),
    CombinationDto(
        combinationId = 2,
        combinationName = "영양 가득 조합",
        price = 98000,
        originalPrice = 120000,
        category = "뷰티",
        likes = 8,
        images = List(3) { dummyImageUrl },
        profileUrl = dummyImageUrl,
        nickname = "헬시걸"
    ),
    CombinationDto(
        combinationId = 3,
        combinationName = "헬스케어 세트",
        price = 250000,
        originalPrice = 300000,
        category = "헬스",
        likes = 25,
        images = List(4) { dummyImageUrl },
        profileUrl = dummyImageUrl,
        nickname = "운동왕"
    )
)
