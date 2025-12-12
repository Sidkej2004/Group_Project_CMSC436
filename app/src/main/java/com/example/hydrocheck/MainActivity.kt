package com.example.hydrocheck

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.hydrocheck.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // check if dark mode is on
        if (Prefs.isDark(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup ads - using test id from google docs
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        // show current water level
        updateUI()

        // button to add water
        binding.add250Btn.setOnClickListener {
            var currentWater = Prefs.getCurrentWater(this)
            currentWater = currentWater + 250
            Prefs.setCurrentWater(this, currentWater)
            updateUI()
        }

        // open map button
        binding.openMapBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        // settings button
        binding.openSettingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI() // refresh when coming back
    }

    // update the progress bar and text
    private fun updateUI() {
        val maxWater = Prefs.getMaxWater(this)
        val currentWater = Prefs.getCurrentWater(this)

        binding.waterProgress.max = maxWater
        binding.waterProgress.progress = currentWater
        binding.progressLabel.text = "$currentWater / $maxWater ml"
    }
}