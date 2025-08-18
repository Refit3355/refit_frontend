package com.refit.app.ui.viewmodel.auth

import java.time.LocalDate

enum class Gender(val code: String) {M("M"), F("F")}

data class SignupUiState (
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val nickname: String = "",
    val memberName: String = "",
    val phoneNumber: String = "",
    val gender: Gender? = null,
    val birthday: LocalDate? = null,
    val zipcode: String = "",
    val roadAddress: String = "",
    val detailAddress: String = "",
    val profileUrl: String = ""
    )