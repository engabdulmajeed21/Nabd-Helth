package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auratech_prefs")

class AuratechDataStore(private val context: Context) {

    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_PHONE = stringPreferencesKey("user_phone")

        val KEY_NOTIF_REMINDERS = booleanPreferencesKey("notif_reminders")
        val KEY_NOTIF_ORDERS = booleanPreferencesKey("notif_orders")
        val KEY_NOTIF_PROMOS = booleanPreferencesKey("notif_promos")
        val KEY_NOTIF_MESSAGES = booleanPreferencesKey("notif_messages")
        val KEY_NOTIF_ALERTS = booleanPreferencesKey("notif_alerts")
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LANGUAGE] ?: "ar"
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME] ?: "system"
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_NAME] ?: "أحمد محمد العولقي"
    }

    val userPhoneFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_PHONE] ?: "777123456"
    }

    val notifRemindersFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_REMINDERS] ?: true }
    val notifOrdersFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_ORDERS] ?: true }
    val notifPromosFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_PROMOS] ?: true }
    val notifMessagesFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_MESSAGES] ?: true }
    val notifAlertsFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_ALERTS] ?: true }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = lang
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = theme
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean, name: String = "أحمد محمد العولقي", phone: String = "777123456") {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = isLoggedIn
            preferences[KEY_USER_NAME] = name
            preferences[KEY_USER_PHONE] = phone
        }
    }

    suspend fun updateNotificationPref(key: String, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            when (key) {
                "reminders" -> prefs[KEY_NOTIF_REMINDERS] = enabled
                "orders" -> prefs[KEY_NOTIF_ORDERS] = enabled
                "promos" -> prefs[KEY_NOTIF_PROMOS] = enabled
                "messages" -> prefs[KEY_NOTIF_MESSAGES] = enabled
                "alerts" -> prefs[KEY_NOTIF_ALERTS] = enabled
            }
        }
    }
}
