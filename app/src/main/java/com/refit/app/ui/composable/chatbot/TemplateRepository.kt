package com.refit.app.ui.composable.chatbot

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

object TemplateRepository {
    private var cache: TemplateBundle? = null
    private val gson = Gson()

    fun loadFromAssets(
        context: Context,
        path: String = "kb/templates.ko.json"
    ): TemplateBundle {
        cache?.let { return it }
        val json = context.assets.open(path).bufferedReader().use(BufferedReader::readText)
        val type = object : TypeToken<TemplateBundle>() {}.type
        return gson.fromJson<TemplateBundle>(json, type).also { cache = it }
    }

    fun clear() { cache = null }
}
