package com.example.hydrocheck

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.hydrocheck.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        HydroController.loadFromPrefs(this)

        if (HydroController.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)
        val ad = AdRequest.Builder().build()
        binding.adView.loadAd(ad)

        updateUI()

        binding.add250Btn.setOnClickListener {
            HydroController.addWater(this, 250)
            updateUI()

            if (HydroController.getCurrentWater() >= HydroController.getMaxWater()) {
                Toast.makeText(this, "Goal reached! 🎉", Toast.LENGTH_SHORT).show()
            }
        }

        binding.openMapBtn.setOnClickListener {
            val i = Intent(this, MapActivity::class.java)
            startActivity(i)
        }

        binding.openSettingsBtn.setOnClickListener {
            val i2 = Intent(this, SettingsActivity::class.java)
            startActivity(i2)
        }

        binding.waterProgress.setOnLongClickListener {
            HydroController.resetWater(this)
            updateUI()
            Toast.makeText(this, "Reset!", Toast.LENGTH_SHORT).show()
            true
        }

        updateFountainCount()
    }

    override fun onResume() {
        super.onResume()
        HydroController.loadFromPrefs(this)
        updateUI()
        updateFountainCount()
    }

    private fun updateUI() {
        val max = HydroController.getMaxWater()
        val current = HydroController.getCurrentWater()

        binding.waterProgress.max = max
        binding.waterProgress.progress = current
        binding.progressLabel.text = "$current / $max ml"
    }

    private fun updateFountainCount() {
        val cnt = HydroController.getFountains().size
        binding.fountainCountText.text = "Community fountains: $cnt"
    }
}