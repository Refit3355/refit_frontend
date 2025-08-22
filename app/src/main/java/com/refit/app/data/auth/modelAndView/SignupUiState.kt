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
    val healthConcerns: Set<String> = emptySet(),

    // 중복검사 상태
    val emailCheckLoading: Boolean = false,
    val emailChecked: Boolean = false,
    val emailAvailable: Boolean = false,
    val emailMsg: String? = null,

    val nickCheckLoading: Boolean = false,
    val nickChecked: Boolean = false,
    val nickAvailable: Boolean = false,
    val nickMsg: String? = null
    )