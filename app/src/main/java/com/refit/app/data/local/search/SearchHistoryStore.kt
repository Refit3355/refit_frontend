package com.refit.app.data.local.search

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.historyDataStore by preferencesDataStore("search_history")
private val KEY_HISTORY_JSON = stringPreferencesKey("keywords_json")

class SearchHistoryStore(
    private val context: Context,
    private val maxSize: Int = 10
) {
    private val json = Json { ignoreUnknownKeys = true }

    // 히스토리 스트림 (최신순 유지)
    val historyFlow = context.historyDataStore.data.map { pref ->
        val raw = pref[KEY_HISTORY_JSON]
        if (raw.isNullOrBlank()) emptyList()
        else runCatching { json.decodeFromString<List<String>>(raw) }.getOrDefault(emptyList())
    }

    // MRU 스택: 중복 제거 후 맨 앞에 추가, 초과분 뒤에서 제거
    suspend fun add(query: String) {
        val q = query.trim()
        if (q.isEmpty()) return
        context.historyDataStore.edit { pref ->
            val cur = pref[KEY_HISTORY_JSON]?.let {
                runCatching { json.decodeFromString<List<String>>(it) }.getOrNull()
            }?.toMutableList() ?: mutableListOf()

            // 중복 제거 후 앞쪽에 삽입
            cur.removeAll { it.equals(q, ignoreCase = false) }
            cur.add(0, q)

            // 최대 개수 제한
            if (cur.size > maxSize) {
                cur.subList(maxSize, cur.size).clear()
            }
            pref[KEY_HISTORY_JSON] = json.encodeToString(cur)
        }
    }

    suspend fun remove(query: String) {
        context.historyDataStore.edit { pref ->
            val cur = pref[KEY_HISTORY_JSON]?.let {
                runCatching { json.decodeFromString<MutableList<String>>(it) }.getOrNull()
            } ?: return@edit
            if (cur.removeAll { it == query }) {
                pref[KEY_HISTORY_JSON] = json.encodeToString(cur)
            }
        }
    }

    suspend fun clear() {
        context.historyDataStore.edit { pref ->
            pref[KEY_HISTORY_JSON] = json.encodeToString(emptyList<String>())
        }
    }
}
