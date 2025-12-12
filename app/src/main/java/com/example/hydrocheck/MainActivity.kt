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
        AppCompatDelegate.setDefaultNightMode(
            if (Prefs.isDark(this)) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ads
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())

        // Init progress UI
        refreshWaterUI()

        binding.add250Btn.setOnClickListener {
            val cur = Prefs.getCurrentWater(this)
            Prefs.setCurrentWater(this, cur + 250)
            refreshWaterUI()
        }

        binding.openMapBtn.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        binding.openSettingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshWaterUI()
    }

    private fun refreshWaterUI() {
        val max = Prefs.getMaxWater(this)
        val cur = Prefs.getCurrentWater(this)

        binding.waterProgress.max = max
        binding.waterProgress.progress = cur
        binding.progressLabel.text = "$cur / $max ml"
    }
}
