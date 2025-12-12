package com.example.hydrocheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.hydrocheck.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            if (Prefs.isDark(this)) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init UI from prefs
        binding.switchDarkMode.isChecked = Prefs.isDark(this)

        val max = Prefs.getMaxWater(this)
        binding.seekMaxWater.progress = max
        binding.txtMaxWater.text = "${max} ml"

        // Dark mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, checked ->
            Prefs.setDark(this, checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Goal seekbar
        binding.seekMaxWater.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, value: Int, fromUser: Boolean) {
                val safe = value.coerceIn(500, 4000)
                binding.txtMaxWater.text = "${safe} ml"
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                val safe = (seekBar?.progress ?: 2000).coerceIn(500, 4000)
                Prefs.setMaxWater(this@SettingsActivity, safe)
                binding.txtMaxWater.text = "${Prefs.getMaxWater(this@SettingsActivity)} ml"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.backToHomeBtn.setOnClickListener {
            finish()
        }
    }
}
