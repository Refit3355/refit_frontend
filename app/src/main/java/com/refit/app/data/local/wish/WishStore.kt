package com.refit.app.data.local.wish

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "wish_prefs")
private val KEY_WISH = stringSetPreferencesKey("wish_ids")

class WishStore(private val context: Context) {

    /** 찜 목록(Long) */
    val wishedIds: Flow<Set<Long>> =
        context.dataStore.data.map { pref ->
            pref[KEY_WISH]
                .orEmpty()
                .mapNotNull { it.toLongOrNull() }
                .toSet()
        }

    /** 토글(Long) */
    suspend fun toggle(id: Long) {
        context.dataStore.edit { pref ->
            val cur = pref[KEY_WISH].orEmpty().toMutableSet()
            val key = id.toString()
            if (!cur.add(key)) cur.remove(key)
            pref[KEY_WISH] = cur
        }
    }

}
