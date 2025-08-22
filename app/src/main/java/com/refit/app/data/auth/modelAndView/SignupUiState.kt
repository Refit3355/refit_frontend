package com.refit.app.data.auth.modelAndView

import java.time.LocalDate

data class SignupUiState (
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val nickname: String = "",
    val memberName: String = "",
    val phoneNumber: String = "",
    val birthday: LocalDate? = null,
    val zipcode: String = "",
    val roadAddress: String = "",
    val detailAddress: String = "",

    val skinType: String? = null,
    val skinConcerns: Set<String> = emptySet(),
    val scalpConcerns: Set<String> = emptySet(),
    val healthConcerns: Set<String> = emptySet()
    )