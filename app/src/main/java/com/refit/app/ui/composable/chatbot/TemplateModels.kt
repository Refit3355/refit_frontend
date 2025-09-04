package com.refit.app.ui.composable.chatbot

data class ChipItem(
    val label: String,
    val next: String? = null,
    val deeplink: String? = null,
    val value: Int? = null,
    val placement: String? = null
)

data class BubbleTemplate(
    val id: String,
    val title: String,
    val blocks: List<String> = emptyList(),
    val bullets: List<String> = emptyList(),
    val chips: List<ChipItem> = emptyList()
)

data class TemplateBundle(
    val templates: List<BubbleTemplate> = emptyList()
) {
    fun get(id: String): BubbleTemplate? = templates.firstOrNull { it.id == id }
}
