package com.example.hydrocheck

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_CODE = 1001
    private val markerToKey = mutableMapOf<Marker, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            map.uiSettings.isMyLocationButtonEnabled = true
            map.uiSettings.isZoomControlsEnabled = true

            checkLocationPermission()

            loadFountainsOnMap()

            map.setOnMapLongClickListener { location ->
                val fountain = Fountain(location.latitude, location.longitude, 0f)

                HydroController.addFountainToFirebase(fountain)

                val marker = map.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Water Fountain")
                        .snippet("Tap to rate")
                )

                Toast.makeText(this, "Fountain added!", Toast.LENGTH_SHORT).show()
            }

            map.setOnMarkerClickListener { marker ->
                showRatingDialog(marker)
                true
            }

            val defaultLocation = LatLng(38.9869, -76.9426)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
        }
    }

    private fun loadFountainsOnMap() {
        HydroController.loadFountainsFromFirebase { fountains ->
            for (fountain in fountains) {
                val position = LatLng(fountain.lat, fountain.lng)
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title("Water Fountain")
                        .snippet("Rating: ${fountain.rating} stars")
                )
            }
        }
    }

    private fun showRatingDialog(marker: Marker) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rate_fountain, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating
                marker.snippet = "Rating: $rating stars"
                marker.showInfoWindow()
                Toast.makeText(this, "Thanks for rating!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission()
            }
        }
    }
}