package com.refit.app.util.common

import com.refit.app.data.auth.model.HealthInfoDto
import com.refit.app.data.auth.model.HairInfoDto
import com.refit.app.data.auth.model.SkinInfoDto

// Health 태그 변환
fun HealthInfoDto.toTags(): List<String> {
    val tags = mutableListOf<String>()
    if (eyeHealth > 0) tags.add("#눈건강")
    if (fatigue > 0) tags.add("#만성 피로")
    if (sleepStress > 0) tags.add("#수면/스트레스")
    if (immuneCare > 0) tags.add("#면역력")
    if (muscleHealth > 0) tags.add("#근력")
    if (gutHealth > 0) tags.add("#장건강")
    if (bloodCirculation > 0) tags.add("#혈액순환")
    return tags
}

// Hair 태그 변환
fun HairInfoDto.toTags(): List<String> {
    val tags = mutableListOf<String>()
    if (hairLoss > 0) tags.add("#탈모")
    if (damagedHair > 0) tags.add("#손상모")
    if (scalpTrouble > 0) tags.add("#두피트러블")
    if (dandruff > 0) tags.add("#비듬/각질")
    return tags
}

// Skin 태그 변환
fun SkinInfoDto.toTags(): List<String> {
    val tags = mutableListOf<String>()
    if (atopic > 0) tags.add("#아토피")
    if (acne > 0) tags.add("#여드름/민감성")
    if (whitening > 0) tags.add("#미백/잡티")
    if (sebum > 0) tags.add("#피지/블랙헤드")
    if (innerDryness > 0) tags.add("#속건조")
    if (wrinkles > 0) tags.add("#주름/탄력")
    if (enlargedPores > 0) tags.add("#모공")
    if (redness > 0) tags.add("#홍조")
    if (keratin > 0) tags.add("#각질")
    skinType?.let {
        when (it) {
            1 -> tags.add("#건성")
            2 -> tags.add("#중성")
            3 -> tags.add("#지성")
            4 -> tags.add("#복합성")
            else -> tags.add("#수부지")
        }
    }
    return tags
}
