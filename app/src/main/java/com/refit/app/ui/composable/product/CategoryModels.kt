package com.refit.app.ui.composable.product

data class Cat(val label: String, val id: Int?) // "전체"는 null

enum class Group(val label: String, val param: String) {
    BEAUTY("뷰티", "beauty"),
    HEALTH("헬스", "health")
}