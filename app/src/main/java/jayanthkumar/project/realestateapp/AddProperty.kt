package jayanthkumar.project.realestateapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

// ✅ Data class for Property
data class Property(
    val id: String = "",
    val propertyName: String = "",
    val contact: String = "",
    val description: String = "",
    val price: String = "",
    val place: String = "",
    val beds: String = "",
    val baths: String = "",
    val area: String = "",
    val type: String = "",
    val amenities: List<String> = emptyList(),
    val imageUrl: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

// ✅ Amenity Model
data class AmenityItem(val id: String, val label: String)

fun allAmenities(): List<AmenityItem> = listOf(
    AmenityItem("wifi", "Wi-Fi"),
    AmenityItem("ac", "AC"),
    AmenityItem("pool", "Pool"),
    AmenityItem("tv", "TV"),
    AmenityItem("parking", "Parking"),
    AmenityItem("bathtub", "Bathtub"),
    AmenityItem("gym", "Gym"),
    AmenityItem("kitchen", "Kitchen"),
    AmenityItem("elevator", "Elevator"),
    AmenityItem("heating", "Heating"),
    AmenityItem("workspace", "Workspace"),
    AmenityItem("pet_friendly", "Pet Friendly"),
    AmenityItem("fireplace", "Fireplace"),
    AmenityItem("pool_table", "Pool Table"),
    AmenityItem("spa", "Spa"),
    AmenityItem("shuttle", "Shuttle"),
    AmenityItem("child_friendly", "Child Friendly")
)

@Composable
fun AmenityChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.White
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray

    Surface(
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = background,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProperty() {
    val context = LocalContext.current

    var propertyName by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var beds by remember { mutableStateOf("") }
    var baths by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    val amenities = remember { allAmenities() }
    val selectedAmenityIds = remember { mutableStateListOf<String>() }

    var imageUri by remember { mutableStateOf<Uri?>(null) }


    val scope = rememberCoroutineScope()

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageUri = it
        }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var selectedAddress by remember { mutableStateOf("") }

    val locationPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                selectedLat = it.data?.getDoubleExtra("lat", 0.0)
                selectedLng = it.data?.getDoubleExtra("lng", 0.0)
                selectedAddress = it.data?.getStringExtra("address") ?: ""
            }
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Property",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.Brown) // ✅ use your custom color here
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Text(if (imageUri == null) "Upload Property Photo" else "Change Photo")
            }

            imageUri?.let {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            // Property Name
            Text("Property Title", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = propertyName,
                onValueChange = { propertyName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter Property title") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))


            Text("Contact Info", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = contactInfo,
                onValueChange = { contactInfo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter Contact") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Description
            Text("Description", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                placeholder = { Text("Enter description") }
            )

            Spacer(Modifier.height(12.dp))

            // Price & Place
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Price", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 2500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Column(Modifier.weight(1f)) {
                    Text("Place", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    PlaceDropdown(
                        selectedPlace = place,
                        onPlaceSelected = { place = it }
                    )
                }
            }


            Spacer(Modifier.height(12.dp))

            // Beds & Baths
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Beds", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = beds,
                        onValueChange = { beds = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 2") },
                        singleLine = true
                    )
                }

                Column(Modifier.weight(1f)) {
                    Text("Baths", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = baths,
                        onValueChange = { baths = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 1") },
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Area & Type
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Area", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 1100 sqft") },
                        singleLine = true
                    )
                }

                Column(Modifier.weight(1f)) {
                    Text("Type", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    PropertyTypeDropdown(
                        selectedType = type,
                        onTypeSelected = { type = it }
                    )
                }
            }


            Spacer(Modifier.height(20.dp))

            // Amenities
            Text("Select Amenities", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                amenities.forEach { amenity ->
                    val selected = amenity.id in selectedAmenityIds
                    AmenityChip(
                        label = amenity.label,
                        selected = selected,
                        onClick = {
                            if (selected) selectedAmenityIds.remove(amenity.id)
                            else selectedAmenityIds.add(amenity.id)
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    locationPicker.launch(
                        Intent(context, LocationPickerActivity::class.java)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
            ) {
                Text(
                    if (selectedAddress.isEmpty())
                        "📍 Select Pickup Location"
                    else
                        "📍 Change Location"
                )
            }


            if (selectedAddress.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    selectedAddress,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = {
                    when {
                        propertyName.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Property name",
                            Toast.LENGTH_SHORT
                        ).show()

                        contactInfo.isBlank() -> Toast.makeText(
                            context,
                            "Please enter ContactInfo",
                            Toast.LENGTH_SHORT
                        ).show()

                        description.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Description",
                            Toast.LENGTH_SHORT
                        ).show()

                        price.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Price",
                            Toast.LENGTH_SHORT
                        ).show()

                        place.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Place",
                            Toast.LENGTH_SHORT
                        ).show()

                        beds.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Beds",
                            Toast.LENGTH_SHORT
                        ).show()

                        baths.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Baths",
                            Toast.LENGTH_SHORT
                        ).show()

                        area.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Area",
                            Toast.LENGTH_SHORT
                        ).show()

                        type.isBlank() -> Toast.makeText(
                            context,
                            "Please enter Type",
                            Toast.LENGTH_SHORT
                        ).show()

                        selectedAmenityIds.isEmpty() -> Toast.makeText(
                            context,
                            "Select at least one Amenity",
                            Toast.LENGTH_SHORT
                        ).show()

                        else -> {
                            scope.launch {
                                val base64 = uriToBase64(context, imageUri!!)
                                val imageUrlPicked = uploadToImgBB(base64)

                                if (imageUrlPicked == null) {
                                    Toast.makeText(
                                        context,
                                        "Image upload failed",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    return@launch
                                }


                                val propertyId = System.currentTimeMillis().toString()
                                val property = Property(
                                    id = propertyId,
                                    propertyName = propertyName,
                                    contact = contactInfo,
                                    description = description,
                                    price = price,
                                    place = place,
                                    beds = beds,
                                    baths = baths,
                                    area = area,
                                    type = type,
                                    amenities = selectedAmenityIds.toList(),
                                    imageUrl = imageUrlPicked,
                                    address = selectedAddress,
                                    latitude = selectedLat!!,
                                    longitude = selectedLng!!
                                )

                                val userName = UserPrefs.getEmail(context).replace(".", ",")

                                val db = FirebaseDatabase.getInstance().getReference("properties")
                                    .child(userName)
                                db.child(propertyId).setValue(property)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Property added successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        (context as? Activity)?.finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Failed to save: ${it.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.Green),
                    contentColor = Color.White
                ),
            ) {
                Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

fun uriToBase64(context: Context, uri: Uri): String {
    val input = context.contentResolver.openInputStream(uri)
    val bytes = input!!.readBytes()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}


private const val IMGBB_API_KEY = "dd2c6f23d315032050b31f06adcfaf3b" // <- your key (from user)

suspend fun uploadToImgBB(base64Image: String): String? = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()

        // ImgBB accepts 'image' param as base64 string
        val form = FormBody.Builder()
            .add("key", IMGBB_API_KEY)
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(form)
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: return@withContext null
            if (!response.isSuccessful) return@withContext null

            val json = JSONObject(body)
            // Look for data -> url or display_url
            val data = json.optJSONObject("data")
            return@withContext data?.optString("url") ?: data?.optString("display_url")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}


@Preview(showBackground = true)
@Composable
fun AddPropertyPreview() {
    MaterialTheme {
        AddProperty()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDropdown(
    selectedPlace: String,
    onPlaceSelected: (String) -> Unit
) {
    val places = ukPlaces()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPlace,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            placeholder = { Text("Select City") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            places.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city) },
                    onClick = {
                        onPlaceSelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = propertyTypes()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            placeholder = { Text("Select Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { propertyType ->
                DropdownMenuItem(
                    text = { Text(propertyType) },
                    onClick = {
                        onTypeSelected(propertyType)
                        expanded = false
                    }
                )
            }
        }
    }
}


fun ukPlaces() = listOf(
    "London",
    "Manchester",
    "Birmingham",
    "Liverpool",
    "Leeds",
    "Sheffield",
    "Bristol",
    "Nottingham",
    "Leicester",
    "Coventry",
    "Oxford",
    "Cambridge",
    "Reading",
    "Milton Keynes",
    "Brighton"
)

fun propertyTypes() = listOf(
    "Apartment",
    "Flat",
    "Villa",
    "Independent House",
    "Studio",
    "Penthouse",
    "Duplex",
    "Townhouse",
    "Bungalow"
)
