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

        val maxWater = HydroController.getMaxWater()
        binding.seekMaxWater.progress = maxWater
        binding.txtMaxWater.text = "${maxWater} ml"

        binding.switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            HydroController.setDarkMode(this, isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Toast.makeText(this, "Dark mode ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        binding.seekMaxWater.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                var value = progress
                if (value < 500) value = 500
                if (value > 4000) value = 4000
                binding.txtMaxWater.text = "${value} ml"

                if (fromUser) {
                    when {
                        value < 1500 -> binding.txtMaxWater.setTextColor(getColor(android.R.color.holo_red_dark))
                        value > 3000 -> binding.txtMaxWater.setTextColor(getColor(android.R.color.holo_blue_dark))
                        else -> binding.txtMaxWater.setTextColor(getColor(android.R.color.holo_green_dark))
                    }
                }
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                val progress = seekBar?.progress ?: 2000
                var value = progress
                if (value < 500) value = 500
                if (value > 4000) value = 4000

                HydroController.setMaxWater(this@SettingsActivity, value)

                val savedValue = HydroController.getMaxWater()
                binding.txtMaxWater.text = "${savedValue} ml"
                binding.txtMaxWater.setTextColor(getColor(android.R.color.black))

                Toast.makeText(this@SettingsActivity, "Goal updated to $savedValue ml", Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                Toast.makeText(this@SettingsActivity, "Adjust your daily goal", Toast.LENGTH_SHORT).show()
            }
        })

        binding.backToHomeBtn.setOnClickListener {
            finish()
        }
    }
}