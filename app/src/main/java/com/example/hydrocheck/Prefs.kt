package com.example.hydrocheck

import android.content.Context

object Prefs {

    private const val FILE = "hydro_prefs"

    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_MAX_WATER = "max_water"
    private const val KEY_CURRENT_WATER = "current_water"

    private const val DEFAULT_MAX = 2000

    private fun sp(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isDark(ctx: Context): Boolean {
        val prefs = sp(ctx)

        return try {
            prefs.getBoolean(KEY_DARK_MODE, false)
        } catch (e: ClassCastException) {
            val raw = prefs.getString(KEY_DARK_MODE, "false") ?: "false"
            val migrated = when (raw.lowercase()) {
                "true", "dark", "1", "yes" -> true
                else -> false
            }
            prefs.edit().remove(KEY_DARK_MODE).putBoolean(KEY_DARK_MODE, migrated).apply()
            migrated
        }
    }

    fun setDark(ctx: Context, dark: Boolean) {
        sp(ctx).edit().putBoolean(KEY_DARK_MODE, dark).apply()
    }

    fun getMaxWater(ctx: Context): Int {
        val prefs = sp(ctx)
        return try {
            val v = prefs.getInt(KEY_MAX_WATER, DEFAULT_MAX)
            if (v <= 0) DEFAULT_MAX else v
        } catch (e: ClassCastException) {
            // If it was ever stored as String, migrate
            val raw = prefs.getString(KEY_MAX_WATER, DEFAULT_MAX.toString()) ?: DEFAULT_MAX.toString()
            val migrated = raw.toIntOrNull() ?: DEFAULT_MAX
            prefs.edit().remove(KEY_MAX_WATER).putInt(KEY_MAX_WATER, migrated).apply()
            migrated
        }
    }

    fun setMaxWater(ctx: Context, value: Int) {
        val v = value.coerceIn(500, 4000)
        sp(ctx).edit().putInt(KEY_MAX_WATER, v).apply()

        val cur = getCurrentWater(ctx)
        if (cur > v) setCurrentWater(ctx, v)
    }

    fun getCurrentWater(ctx: Context): Int {
        val max = getMaxWater(ctx)
        val prefs = sp(ctx)
        val v = try {
            prefs.getInt(KEY_CURRENT_WATER, 0)
        } catch (e: ClassCastException) {
            val raw = prefs.getString(KEY_CURRENT_WATER, "0") ?: "0"
            val migrated = raw.toIntOrNull() ?: 0
            prefs.edit().remove(KEY_CURRENT_WATER).putInt(KEY_CURRENT_WATER, migrated).apply()
            migrated
        }
        return v.coerceIn(0, max)
    }

    fun setCurrentWater(ctx: Context, value: Int) {
        val max = getMaxWater(ctx)
        val v = value.coerceIn(0, max)
        sp(ctx).edit().putInt(KEY_CURRENT_WATER, v).apply()
    }
}
