package jayanthkumar.project.realestateapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MapPickerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MapPickerScreen(
                    onLocationSelected = { lat, lng ->
                        val intent = Intent().apply {
                            putExtra("lat", lat)
                            putExtra("lng", lng)
                        }
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                )
            }
        }
    }
}


@Composable
fun MapPickerScreen(
    onLocationSelected: (Double, Double) -> Unit
) {
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    val indiaCenter = LatLng(20.5937, 78.9629)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(indiaCenter, 5f)
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    selectedLatLng?.let {
                        onLocationSelected(it.latitude, it.longitude)
                    }
                },
                enabled = selectedLatLng != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = if (selectedLatLng == null)
                        "Tap on map to choose location"
                    else
                        "Confirm Location"
                )
            }
        }
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLatLng = latLng
                }
            ) {
                selectedLatLng?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Selected Location"
                    )
                }
            }
        }
    }
}
