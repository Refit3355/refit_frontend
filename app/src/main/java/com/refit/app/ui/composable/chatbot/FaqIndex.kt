package com.refit.app.ui.composable.chatbot

data class FaqEntry(
    val id: String,
    val question: String,
    val keywords: List<String>
)

object FaqIndex {
    val entries = listOf(
        FaqEntry("faq_q1",  "기초 화장품 유통기한 (미개봉)", listOf("유통기한", "미개봉", "기초", "스킨", "로션", "크림")),
        FaqEntry("faq_q2",  "개봉 후 사용 기한", listOf("개봉", "사용기한", "6개월", "12개월")),
        FaqEntry("faq_q3",  "냉장 보관 가능 여부", listOf("냉장", "보관", "상온", "여름")),
        FaqEntry("faq_q4",  "로션과 크림 차이", listOf("로션", "크림", "보습", "유분", "수분")),
        FaqEntry("faq_q5",  "에센스/세럼/앰플 차이", listOf("에센스", "세럼", "앰플", "농축", "기능성")),
        FaqEntry("faq_q6",  "스킨(토너) vs 로션", listOf("스킨", "토너", "로션", "1단계", "보습막")),
        FaqEntry("faq_q7",  "영양제 유통기한 (미개봉)", listOf("영양제", "유통기한", "비타민", "오메가")),
        FaqEntry("faq_q8",  "영양제 냉장 보관", listOf("영양제", "유산균", "냉장", "보관")),
        FaqEntry("faq_q9",  "영양제 동시 복용", listOf("같이", "동시", "성분", "중복", "과잉")),
        FaqEntry("faq_q10", "폐기 시점", listOf("버리", "폐기", "변색", "냄새", "분리")),
        FaqEntry("faq_q11", "화장품 혼합 사용", listOf("섞", "레티놀", "비타민C", "자극")),
        FaqEntry("faq_q12", "영양제 복용 시간 (아침 vs 저녁)", listOf("아침", "저녁", "칼슘", "마그네슘", "오메가")),
        FaqEntry("faq_q13", "빈속 복용 가능 여부", listOf("빈속", "식후", "지용성", "오메가3")),
        FaqEntry("faq_q14", "물 대신 다른 음료", listOf("물", "커피", "녹차", "탄산", "철분", "흡수"))
    )

    // 매칭
    fun suggest(query: String, topN: Int = 5): List<FaqEntry> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()
        return entries
            .map { e ->
                val score = e.keywords.count { k -> q.contains(k.lowercase()) } +
                        (if (e.question.lowercase().contains(q)) 2 else 0)
                e to score
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(topN)
            .map { it.first }
    }
}
