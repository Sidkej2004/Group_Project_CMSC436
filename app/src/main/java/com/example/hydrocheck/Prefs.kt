package com.example.hydrocheck

import android.content.Context

// helper object for shared preferences
object Prefs {

    private const val PREFS_NAME = "hydro_prefs"
    private const val DARK_MODE_KEY = "dark_mode"
    private const val MAX_WATER_KEY = "max_water"
    private const val CURRENT_WATER_KEY = "current_water"

    // get shared preferences
    private fun getPrefs(ctx: Context) = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // dark mode functions
    fun isDark(ctx: Context): Boolean {
        val prefs = getPrefs(ctx)
        return try {
            prefs.getBoolean(DARK_MODE_KEY, false)
        } catch (e: ClassCastException) {
            // fix if it was saved as string
            val stringValue = prefs.getString(DARK_MODE_KEY, "false") ?: "false"
            val boolValue = when (stringValue.lowercase()) {
                "true", "dark", "1", "yes" -> true
                else -> false
            }
            prefs.edit().remove(DARK_MODE_KEY).putBoolean(DARK_MODE_KEY, boolValue).apply()
            boolValue
        }
    }

    fun setDark(ctx: Context, isDark: Boolean) {
        getPrefs(ctx).edit().putBoolean(DARK_MODE_KEY, isDark).apply()
    }

    // max water goal functions
    fun getMaxWater(ctx: Context): Int {
        val prefs = getPrefs(ctx)
        return try {
            val value = prefs.getInt(MAX_WATER_KEY, 2000)
            if (value <= 0) 2000 else value
        } catch (e: ClassCastException) {
            // migrate from string if needed
            val stringValue = prefs.getString(MAX_WATER_KEY, "2000") ?: "2000"
            val intValue = stringValue.toIntOrNull() ?: 2000
            prefs.edit().remove(MAX_WATER_KEY).putInt(MAX_WATER_KEY, intValue).apply()
            intValue
        }
    }

    fun setMaxWater(ctx: Context, value: Int) {
        // keep between 500 and 4000
        var safeValue = value
        if (safeValue < 500) safeValue = 500
        if (safeValue > 4000) safeValue = 4000

        getPrefs(ctx).edit().putInt(MAX_WATER_KEY, safeValue).apply()

        // make sure current water doesn't exceed max
        val currentWater = getCurrentWater(ctx)
        if (currentWater > safeValue) {
            setCurrentWater(ctx, safeValue)
        }
    }

    // current water functions
    fun getCurrentWater(ctx: Context): Int {
        val maxWater = getMaxWater(ctx)
        val prefs = getPrefs(ctx)
        val value = try {
            prefs.getInt(CURRENT_WATER_KEY, 0)
        } catch (e: ClassCastException) {
            // fix string values
            val stringValue = prefs.getString(CURRENT_WATER_KEY, "0") ?: "0"
            val intValue = stringValue.toIntOrNull() ?: 0
            prefs.edit().remove(CURRENT_WATER_KEY).putInt(CURRENT_WATER_KEY, intValue).apply()
            intValue
        }

        // make sure it's not negative or over max
        if (value < 0) return 0
        if (value > maxWater) return maxWater
        return value
    }

    fun setCurrentWater(ctx: Context, value: Int) {
        val maxWater = getMaxWater(ctx)
        var safeValue = value

        // keep between 0 and max
        if (safeValue < 0) safeValue = 0
        if (safeValue > maxWater) safeValue = maxWater

        getPrefs(ctx).edit().putInt(CURRENT_WATER_KEY, safeValue).apply()
    }
}