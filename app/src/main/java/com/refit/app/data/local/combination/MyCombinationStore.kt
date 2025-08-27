package com.refit.app.data.local.combination

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "my_combination_prefs")
private val KEY_MY_COMBINATION = stringSetPreferencesKey("my_combination_ids")

class MyCombinationStore(private val context: Context) {

    /** 저장된 조합 목록(Long) */
    val savedIds: Flow<Set<Long>> =
        context.dataStore.data.map { pref ->
            pref[KEY_MY_COMBINATION]
                .orEmpty()
                .mapNotNull { it.toLongOrNull() }
                .toSet()
        }

    /** 토글(Long) */
    suspend fun toggle(id: Long) {
        context.dataStore.edit { pref ->
            val cur = pref[KEY_MY_COMBINATION].orEmpty().toMutableSet()
            val key = id.toString()
            if (!cur.add(key)) cur.remove(key)
            pref[KEY_MY_COMBINATION] = cur
        }
    }
}
