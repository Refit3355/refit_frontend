package com.refit.app.ui.composable.common

data class ChatCategory(val id: Long, val title: String, val subtitle: String)

val beautyRooms = listOf(
    ChatCategory(0, "스킨/토너", ""),
    ChatCategory(1, "에센스/세럼/앰플", ""),
    ChatCategory(2, "크림", ""),
    ChatCategory(3, "로션/바디로션", ""),
    ChatCategory(4, "미스트", ""),
    ChatCategory(5, "오일", ""),
    ChatCategory(6, "샴푸/린스/트리트먼트", ""),
    ChatCategory(7, "헤어케어", "")
)
val healthRooms = listOf(
    ChatCategory(8,  "비타민", ""),
    ChatCategory(9,  "오메가/루테인", ""),
    ChatCategory(10, "칼슘/마그네슘/철분", ""),
    ChatCategory(11, "유산균", "")
)
val allRooms = beautyRooms + healthRooms
