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

    private lateinit var gmap: GoogleMap
    private val PERM_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val frag = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment

        frag.getMapAsync { map ->
            gmap = map

            gmap.uiSettings.isMyLocationButtonEnabled = true
            gmap.uiSettings.isZoomControlsEnabled = true

            checkPermission()

            loadFountains()

            gmap.setOnMapLongClickListener { pos ->
                val f = Fountain(pos.latitude, pos.longitude, 0f)

                HydroController.addFountainToFirebase(f)

                val m = gmap.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title("Water Fountain")
                        .snippet("Tap to rate")
                )

                Toast.makeText(this, "Fountain added!", Toast.LENGTH_SHORT).show()
            }

            gmap.setOnMarkerClickListener { marker ->
                showRatingDialog(marker)
                true
            }

            val defLoc = LatLng(38.9869, -76.9426)
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(defLoc, 15f))
        }
    }

    private fun loadFountains() {
        HydroController.loadFountainsFromFirebase { fountainList ->
            for (f in fountainList) {
                val pos = LatLng(f.lat, f.lng)
                gmap.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title("Water Fountain")
                        .snippet("Rating: ${f.rating} stars")
                )
            }
        }
    }

    private fun showRatingDialog(marker: Marker) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_rate_fountain, null)
        val rBar = view.findViewById<RatingBar>(R.id.ratingBar)

        val d = AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Submit") { _, _ ->
                val r = rBar.rating
                marker.snippet = "Rating: $r stars"
                marker.showInfoWindow()
                Toast.makeText(this, "Thanks!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        d.show()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            gmap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERM_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERM_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission()
            }
        }
    }
}