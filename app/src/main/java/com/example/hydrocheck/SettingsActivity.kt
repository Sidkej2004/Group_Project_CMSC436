package com.example.hydrocheck

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.hydrocheck.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        HydroController.loadFromPrefs(this)

        if (HydroController.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchDarkMode.isChecked = HydroController.isDarkMode()

        val m = HydroController.getMaxWater()
        binding.seekMaxWater.progress = m
        binding.txtMaxWater.text = "${m} ml"

        binding.switchDarkMode.setOnCheckedChangeListener { v, checked ->
            HydroController.setDarkMode(this, checked)
            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Toast.makeText(this, "Dark mode ${if (checked) "on" else "off"}", Toast.LENGTH_SHORT).show()
        }

        binding.seekMaxWater.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, prog: Int, fromUser: Boolean) {
                var v = prog
                if (v < 500) v = 500
                if (v > 4000) v = 4000
                binding.txtMaxWater.text = "${v} ml"

                if (fromUser) {
                    when {
                        v < 1500 -> binding.txtMaxWater.setTextColor(getColor(android.R.color.holo_red_dark))
                        v > 3000 -> binding.txtMaxWater.setTextColor(getColor(android.R.color.holo_blue_dark))
                        else -> binding.txtMaxWater.setTextColor(getColor(android.R.color.holo_green_dark))
                    }
                }
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                val p = seekBar?.progress ?: 2000
                var v = p
                if (v < 500) v = 500
                if (v > 4000) v = 4000

                HydroController.setMaxWater(this@SettingsActivity, v)

                val saved = HydroController.getMaxWater()
                binding.txtMaxWater.text = "${saved} ml"
                binding.txtMaxWater.setTextColor(getColor(android.R.color.black))

                Toast.makeText(this@SettingsActivity, "Goal: $saved ml", Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                Toast.makeText(this@SettingsActivity, "Drag to adjust", Toast.LENGTH_SHORT).show()
            }
        })

        binding.backToHomeBtn.setOnClickListener {
            finish()
        }
    }
}