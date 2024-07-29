package com.example.firebase

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            enableMyLocation()
            Log.d("MapActivity", "Location permission Enabled")

        } else {
            Log.d("MapActivity", "Location permission denied")
        }
    }

    private val delhi: LatLng = LatLng(28.644800, 77.216721)

    private lateinit var hybridMapBtn: Button
    private lateinit var terrainMapBtn: Button
    private lateinit var satelliteMapBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        hybridMapBtn = findViewById(R.id.idBtnHybridMap)
        terrainMapBtn = findViewById(R.id.idBtnTerrainMap)
        satelliteMapBtn = findViewById(R.id.idBtnSatelliteMap)

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        hybridMapBtn.setOnClickListener {
            mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
        }

        terrainMapBtn.setOnClickListener {
            mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }

        satelliteMapBtn.setOnClickListener {
            mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        mMap?.addMarker(MarkerOptions().position(delhi).title("Marker in Delhi"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(delhi))
    }

    private fun enableMyLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap?.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        Log.d("MapActivity", "Location found: $currentLatLng")
                    } else {
                        Log.d("MapActivity", "Location is null")
                    }
                } else {
                    Log.d("MapActivity", "Failed to get location")
                }
            }
        } else {
            Log.d("MapActivity", "Location permission not granted")
        }
    }
}
