package com.example.hydrocheck

import android.content.Context

object Prefs {

    private const val FILE = "hydro_prefs"
    private const val KEY_DARK = "dark_mode"
    private const val KEY_MAX = "max_water"
    private const val KEY_CUR = "current_water"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isDark(ctx: Context): Boolean {
        val prefs = sp(ctx)
        return try {
            prefs.getBoolean(KEY_DARK, false)
        } catch (e: ClassCastException) {
            val s = prefs.getString(KEY_DARK, "false") ?: "false"
            val b = when (s.lowercase()) {
                "true", "dark", "1", "yes" -> true
                else -> false
            }
            prefs.edit().remove(KEY_DARK).putBoolean(KEY_DARK, b).apply()
            b
        }
    }

    fun setDark(ctx: Context, dark: Boolean) {
        sp(ctx).edit().putBoolean(KEY_DARK, dark).apply()
    }

    fun getMaxWater(ctx: Context): Int {
        val prefs = sp(ctx)
        return try {
            val v = prefs.getInt(KEY_MAX, 2000)
            if (v <= 0) 2000 else v
        } catch (e: ClassCastException) {
            val s = prefs.getString(KEY_MAX, "2000") ?: "2000"
            val i = s.toIntOrNull() ?: 2000
            prefs.edit().remove(KEY_MAX).putInt(KEY_MAX, i).apply()
            i
        }
    }

    fun setMaxWater(ctx: Context, value: Int) {
        var v = value
        if (v < 500) v = 500
        if (v > 4000) v = 4000

        sp(ctx).edit().putInt(KEY_MAX, v).apply()
    }

    fun getCurrentWater(ctx: Context): Int {
        val max = getMaxWater(ctx)
        val prefs = sp(ctx)
        val v = try {
            prefs.getInt(KEY_CUR, 0)
        } catch (e: ClassCastException) {
            val s = prefs.getString(KEY_CUR, "0") ?: "0"
            val i = s.toIntOrNull() ?: 0
            prefs.edit().remove(KEY_CUR).putInt(KEY_CUR, i).apply()
            i
        }

        if (v < 0) return 0
        if (v > max) return max
        return v
    }

    fun setCurrentWater(ctx: Context, value: Int) {
        val max = getMaxWater(ctx)
        var v = value

        if (v < 0) v = 0
        if (v > max) v = max

        sp(ctx).edit().putInt(KEY_CUR, v).apply()
    }
}