package jayanthkumar.project.realestateapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale
import kotlin.apply
import kotlin.collections.firstOrNull
import kotlin.let

class LocationPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            LocationPickerScreen()
            LocationPickerScreen(
                onLocationPicked = { lat, lng, address ->
                    val result = Intent().apply {
                        putExtra("lat", lat)
                        putExtra("lng", lng)
                        putExtra("address", address)
                    }
                    setResult(RESULT_OK, result)
                    finish()
                },
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    onLocationPicked: (Double, Double, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var address by remember { mutableStateOf("") }
    var hasPermission by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
            if (!granted) {
                Toast.makeText(context, "Location permission required", Toast.LENGTH_LONG).show()
            }
        }

    // 🔹 Ask permission + ensure location enabled
    LaunchedEffect(Unit) {

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return@LaunchedEffect
        }

        hasPermission = true

        if (!isLocationEnabled(context)) {
            Toast.makeText(context, "Please turn on location", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return@LaunchedEffect
        }

        // 🔹 Get current location properly
        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                selectedLatLng = latLng
                address = getAddress(context, latLng)


                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    latLng,
                    17f
                )


                // 🔥 AUTO ZOOM (THIS FIXES YOUR ISSUE)
//                cameraPositionState.animate(
//                    CameraUpdateFactory.newLatLngZoom(latLng, 17f),
//                    1000
//                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Pickup Location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedLatLng?.let {
                        onLocationPicked(it.latitude, it.longitude, address)
                    }
                },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White)
            }
        }
    ) { padding ->

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasPermission
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = true
            ),
            onMapClick = { latLng ->
                selectedLatLng = latLng
                address = getAddress(context, latLng)
            }
        ) {
            selectedLatLng?.let {
                Marker(
                    state = MarkerState(it),
                    title = "Pickup Location",
                    snippet = address
                )
            }
        }
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreenOld(
    onLocationPicked: (Double, Double, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    val fusedClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val settingsClient = remember {
        LocationServices.getSettingsClient(context)
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var address by remember { mutableStateOf("") }
    var hasPermission by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) {
                Toast.makeText(context, "Location permission required", Toast.LENGTH_LONG).show()
            }
        }


    // 🔹 Permission launcher
//    val permissionLauncher =
//        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            hasPermission = granted
//        }

    // 🔹 Ask permission on start
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // 🔹 Enable location + get current location
    LaunchedEffect(hasPermission) {
        if (!hasPermission) return@LaunchedEffect

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                // ✅ Location services ON → fetch location
                fusedClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        selectedLatLng = latLng
                        address = getAddress(context, latLng)

                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(latLng, 17f)
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // 🔥 Ask user to turn ON location
                    exception.startResolutionForResult(activity, 1001)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Pickup Location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedLatLng?.let {
                        onLocationPicked(
                            it.latitude,
                            it.longitude,
                            address
                        )
                    }
                },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasPermission
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false
            ),
            onMapClick = { latLng ->
                selectedLatLng = latLng
                address = getAddress(context, latLng)
            }
        ) {
            selectedLatLng?.let {
                Marker(
                    state = MarkerState(it),
                    title = "Pickup Location",
                    snippet = address
                )
            }
        }
    }
}




fun getAddress(context: Context, latLng: LatLng): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1
        )
        addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown address"
    } catch (e: Exception) {
        "Unable to fetch address"
    }
}

