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
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        updateUI()

        binding.add250Btn.setOnClickListener {
            HydroController.addWater(this, 250)
            updateUI()

            if (HydroController.getCurrentWater() >= HydroController.getMaxWater()) {
                Toast.makeText(this, "Daily goal reached! 🎉", Toast.LENGTH_SHORT).show()
            }
        }

        binding.openMapBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        binding.openSettingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        binding.waterProgress.setOnLongClickListener {
            HydroController.resetWater(this)
            updateUI()
            Toast.makeText(this, "Water intake reset", Toast.LENGTH_SHORT).show()
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
        val maxWater = HydroController.getMaxWater()
        val currentWater = HydroController.getCurrentWater()

        binding.waterProgress.max = maxWater
        binding.waterProgress.progress = currentWater
        binding.progressLabel.text = "$currentWater / $maxWater ml"
    }

    private fun updateFountainCount() {
        val count = HydroController.getFountains().size
        binding.fountainCountText.text = "Community fountains: $count"
    }
}