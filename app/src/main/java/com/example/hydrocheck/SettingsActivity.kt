package com.example.hydrocheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.hydrocheck.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // set dark mode before onCreate
        if (Prefs.isDark(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup dark mode switch
        binding.switchDarkMode.isChecked = Prefs.isDark(this)

        // setup water goal seekbar
        val maxWater = Prefs.getMaxWater(this)
        binding.seekMaxWater.progress = maxWater
        binding.txtMaxWater.text = "${maxWater} ml"

        // when dark mode switch changes
        binding.switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            Prefs.setDark(this, isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // seekbar listener
        binding.seekMaxWater.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                // make sure value is between 500 and 4000
                var value = progress
                if (value < 500) value = 500
                if (value > 4000) value = 4000
                binding.txtMaxWater.text = "${value} ml"
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                // save when user stops dragging
                val progress = seekBar?.progress ?: 2000
                var value = progress
                if (value < 500) value = 500
                if (value > 4000) value = 4000
                Prefs.setMaxWater(this@SettingsActivity, value)
                val savedValue = Prefs.getMaxWater(this@SettingsActivity)
                binding.txtMaxWater.text = "${savedValue} ml"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                // don't need to do anything here
            }
        })

        // back button
        binding.backToHomeBtn.setOnClickListener {
            finish()
        }
    }
}