package com.refit.app.ui.fake

// 더미 멤버 데이터 (향후 API 연동 시 대체)
object MemberDummy {
    val member = Member(
        id = "1001",
        nickname = "외식고기",
        profileUrl = "https://cdn.app/p/1001.png",
        concern1 = "속건조",
        concern2 = "홍조",
        concern3 = "각질"
    )
}

data class Member(
    val id: String,
    val nickname: String,
    val profileUrl: String,
    val concern1: String,
    val concern2: String,
    val concern3: String
)
