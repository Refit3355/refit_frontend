package com.refit.app.data.myfit.model

/* 중복 방지용 공통 타입 */
data class MyfitEffect(val id: Int, val name: String, val type: String) // type: "beauty" | "health"
data class MyfitCategory(val id: Int, val name: String, val type: String)

object MyfitDataSource {
    const val TYPE_BEAUTY = "beauty"
    const val TYPE_HEALTH = "health"

    // 효과
    val EFFECTS: List<MyfitEffect> = listOf(
        MyfitEffect(0,  "보습",        TYPE_BEAUTY),
        MyfitEffect(1,  "진정",        TYPE_BEAUTY),
        MyfitEffect(2,  "주름 개선",   TYPE_BEAUTY),
        MyfitEffect(3,  "미백",        TYPE_BEAUTY),
        MyfitEffect(4,  "자외선 차단", TYPE_BEAUTY),
        MyfitEffect(5,  "여드름 완화", TYPE_BEAUTY),
        MyfitEffect(6,  "가려움 개선", TYPE_BEAUTY),
        MyfitEffect(7,  "튼살 개선",   TYPE_BEAUTY),
        MyfitEffect(8,  "손상모 개선", TYPE_BEAUTY),
        MyfitEffect(9,  "탈모 개선",   TYPE_BEAUTY),
        MyfitEffect(10, "두피 개선",   TYPE_BEAUTY),
        MyfitEffect(11, "혈행 개선",   TYPE_HEALTH),
        MyfitEffect(12, "장 건강",     TYPE_HEALTH),
        MyfitEffect(13, "면역력 증진", TYPE_HEALTH),
        MyfitEffect(14, "항산화",      TYPE_HEALTH),
        MyfitEffect(15, "눈 건강",     TYPE_HEALTH),
        MyfitEffect(16, "뼈 건강",     TYPE_HEALTH),
        MyfitEffect(17, "활력",        TYPE_HEALTH),
        MyfitEffect(18, "피부 건강",   TYPE_HEALTH),
    )

    // 카테고리
    val CATEGORIES: List<MyfitCategory> = listOf(
        MyfitCategory(0,  "스킨/토너",            TYPE_BEAUTY),
        MyfitCategory(1,  "에센스/세럼/앰플",     TYPE_BEAUTY),
        MyfitCategory(2,  "크림",                TYPE_BEAUTY),
        MyfitCategory(3,  "로션/바디로션",        TYPE_BEAUTY),
        MyfitCategory(4,  "미스트",              TYPE_BEAUTY),
        MyfitCategory(5,  "오일",                TYPE_BEAUTY),
        MyfitCategory(6,  "샴푸/린스/트리트먼트", TYPE_BEAUTY),
        MyfitCategory(7,  "헤어케어",            TYPE_BEAUTY),
        MyfitCategory(8,  "비타민",              TYPE_HEALTH),
        MyfitCategory(9,  "오메가/루테인",        TYPE_HEALTH),
        MyfitCategory(10, "칼슘/마그네슘/철분",   TYPE_HEALTH),
        MyfitCategory(11, "유산균",              TYPE_HEALTH),
    )

    // 헬퍼
    fun effectsFor(type: String) = EFFECTS.filter { it.type == type }
    fun categoriesFor(type: String) = CATEGORIES.filter { it.type == type }

    // 섹션 단위(등록/수정 UI에서 사용)
    fun sectionedEffects(type: String): List<Pair<String, List<MyfitEffect>>> =
        if (type == TYPE_BEAUTY)
            listOf(
                "피부" to EFFECTS.filter { it.id in 0..7 },
                "헤어" to EFFECTS.filter { it.id in 8..10 }
            )
        else
            listOf("건강" to EFFECTS.filter { it.id in 11..18 })
}