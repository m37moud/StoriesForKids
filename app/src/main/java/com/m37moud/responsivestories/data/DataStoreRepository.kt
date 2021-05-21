package com.m37moud.responsivestories.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.preferencesKey
import com.m37moud.responsivestories.util.Constants.Companion.PREFERENCES_BACK_ONLINE
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.m37moud.responsivestories.util.Constants.Companion.PREFERENCES_DOWNLOAD_STATUS
import com.m37moud.responsivestories.util.Constants.Companion.PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {

        val backOnline = preferencesKey<Boolean>(PREFERENCES_BACK_ONLINE)
        val downloadStatus = preferencesKey<Boolean>(PREFERENCES_DOWNLOAD_STATUS)

    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCES_NAME
    )



    suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }

    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline
        }

//download status
    suspend fun saveDownloadStatus(downloadStatus: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.downloadStatus] = downloadStatus
        }
    }

    val readDownloadStatus: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

        .map { preferences ->
            val downloadStatus = preferences[PreferenceKeys.downloadStatus] ?: false
            downloadStatus
        }
}