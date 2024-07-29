package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Arrays

class Map2 : AppCompatActivity() {

    private lateinit var placesClient: PlacesClient
    private val AUTOCOMPLETE_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map2)

        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyB9PLO8-dXg8nQ60kL0_J5wpjeafsfIKaQ")
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this)

        // Uncomment one of the following methods: AutocompleteSupportFragment or AutocompleteActivity

        // Using AutocompleteSupportFragment:
        setupAutocompleteSupportFragment()

        // OR

        // Using AutocompleteActivity:
        // startAutocompleteActivity()
    }

    // Using AutocompleteSupportFragment
    private fun setupAutocompleteSupportFragment() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(listOf(Field.ID, Field.NAME, Field.ADDRESS))

        // Set up a Place Selection Listener
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Handle the selected place
                Log.i("TAG", "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // Handle the error
                Log.i("TAG", "An error occurred: $status")
            }
        })
    }

    // Using AutocompleteActivity
    private fun startAutocompleteActivity() {
        val fields = listOf(Field.ID, Field.NAME)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                Log.i("TAG", "Place: ${place.name}, ${place.id}")
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error
                val status = Autocomplete.getStatusFromIntent(data)
                Log.i("TAG", status.statusMessage!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation
            }
        }
    }

    fun fetchPlace() {
        // Define a Place ID.
        val placeId = "INSERT_PLACE_ID_HERE"

        // Specify the fields to return.
        val placeFields = listOf(Field.ID, Field.NAME)

        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .build()

        // Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place
            Log.i("TAG", "Place found: ${place.name}")
        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                val apiException = exception as ApiException
                val statusCode = apiException.statusCode
                // Handle error with given status code.
                Log.e("TAG", "Place not found: ${exception.message}")
            }
        }
    }
}
