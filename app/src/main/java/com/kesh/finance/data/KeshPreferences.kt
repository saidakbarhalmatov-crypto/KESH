package com.kesh.finance.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeshPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("kesh_prefs", Context.MODE_PRIVATE)

    private val _selectedThemeId = MutableStateFlow(prefs.getString("theme_id", "itachi") ?: "itachi")
    val selectedThemeId: StateFlow<String> = _selectedThemeId.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Саидакбар") ?: "Саидакбар")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _monthlyBudget = MutableStateFlow(prefs.getFloat("monthly_budget", 35000f).toDouble())
    val monthlyBudget: StateFlow<Double> = _monthlyBudget.asStateFlow()

    private val _autoBankEnabled = MutableStateFlow(prefs.getBoolean("auto_bank_enabled", true))
    val autoBankEnabled: StateFlow<Boolean> = _autoBankEnabled.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(prefs.getString("gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    fun setTheme(id: String) {
        prefs.edit().putString("theme_id", id).apply()
        _selectedThemeId.value = id
    }

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
        _userName.value = name
    }

    fun setMonthlyBudget(budget: Double) {
        prefs.edit().putFloat("monthly_budget", budget.toFloat()).apply()
        _monthlyBudget.value = budget
    }

    fun setAutoBankEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_bank_enabled", enabled).apply()
        _autoBankEnabled.value = enabled
    }

    fun setGeminiApiKey(key: String) {
        val trimmed = key.trim()
        prefs.edit().putString("gemini_api_key", trimmed).apply()
        _geminiApiKey.value = trimmed
    }
}
