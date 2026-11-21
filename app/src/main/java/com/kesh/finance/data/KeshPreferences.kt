package com.kesh.finance.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeshPreferences(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("kesh_pro_prefs", Context.MODE_PRIVATE)

    private val _selectedThemeId = MutableStateFlow(
        prefs.getString("theme_id", "royal_gold") ?: "royal_gold"
    )
    val selectedThemeId: StateFlow<String> = _selectedThemeId.asStateFlow()

    private val _userName = MutableStateFlow(
        prefs.getString("user_name", "Саидакбар") ?: "Саидакбар"
    )
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _monthlyBudget = MutableStateFlow(
        prefs.getFloat("monthly_budget", 35000f).toDouble()
    )
    val monthlyBudget: StateFlow<Double> = _monthlyBudget.asStateFlow()

    private val _autoBankEnabled = MutableStateFlow(
        prefs.getBoolean("auto_bank_enabled", true)
    )
    val autoBankEnabled: StateFlow<Boolean> = _autoBankEnabled.asStateFlow()

    fun setTheme(id: String) {
        prefs.edit().putString("theme_id", id).apply()
        _selectedThemeId.value = id
    }

    fun setUserName(name: String) {
        val cleaned = name.trim().ifEmpty { "Саидакбар" }
        prefs.edit().putString("user_name", cleaned).apply()
        _userName.value = cleaned
    }

    fun setMonthlyBudget(amount: Double) {
        val valid = if (amount > 0) amount else 35000.0
        prefs.edit().putFloat("monthly_budget", valid.toFloat()).apply()
        _monthlyBudget.value = valid
    }

    fun setAutoBankEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_bank_enabled", enabled).apply()
        _autoBankEnabled.value = enabled
    }
}
