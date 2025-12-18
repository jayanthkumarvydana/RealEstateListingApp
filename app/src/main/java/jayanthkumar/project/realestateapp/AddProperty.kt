package jayanthkumar.project.realestateapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

// ✅ Data class for Property
data class Property(
    val id: String = "",
    val propertyName: String = "",
    val contact: String="",
    val description: String = "",
    val price: String = "",
    val place: String = "",
    val beds: String = "",
    val baths: String = "",
    val area: String = "",
    val type: String = "",
    val amenities: List<String> = emptyList()
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
    val background = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.White
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
            modifier = Modifier.padding(horizontal = 12.dp).fillMaxHeight(),
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

@Composable
fun SelectedAmenityChip(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Add Property", fontWeight = FontWeight.SemiBold, fontSize = 22.sp,color = Color.White)
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
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                placeholder = { Text("Enter description") }
            )

            Spacer(Modifier.height(12.dp))

            // Price & Place
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Price", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., 2500") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Column(Modifier.weight(1f)) {
                    Text("Place", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = place,
                        onValueChange = { place = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., London") },
                        singleLine = true
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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., Villa") },
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Amenities
            Text("Select Amenities", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
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

            // ✅ Post Button (Firebase save)
            Button(
                onClick = {
                    when {
                        propertyName.isBlank() -> Toast.makeText(context, "Please enter Property name", Toast.LENGTH_SHORT).show()
                        contactInfo.isBlank() -> Toast.makeText(context, "Please enter ContactInfo", Toast.LENGTH_SHORT).show()
                        description.isBlank() -> Toast.makeText(context, "Please enter Description", Toast.LENGTH_SHORT).show()
                        price.isBlank() -> Toast.makeText(context, "Please enter Price", Toast.LENGTH_SHORT).show()
                        place.isBlank() -> Toast.makeText(context, "Please enter Place", Toast.LENGTH_SHORT).show()
                        beds.isBlank() -> Toast.makeText(context, "Please enter Beds", Toast.LENGTH_SHORT).show()
                        baths.isBlank() -> Toast.makeText(context, "Please enter Baths", Toast.LENGTH_SHORT).show()
                        area.isBlank() -> Toast.makeText(context, "Please enter Area", Toast.LENGTH_SHORT).show()
                        type.isBlank() -> Toast.makeText(context, "Please enter Type", Toast.LENGTH_SHORT).show()
                        selectedAmenityIds.isEmpty() -> Toast.makeText(context, "Select at least one Amenity", Toast.LENGTH_SHORT).show()
                        else -> {
                            val propertyId = System.currentTimeMillis().toString()
                            val property = Property(
                                id = propertyId,
                                propertyName = propertyName,
                                contact=contactInfo,
                                description = description,
                                price = price,
                                place = place,
                                beds = beds,
                                baths = baths,
                                area = area,
                                type = type,
                                amenities = selectedAmenityIds.toList()
                            )

                            val db = FirebaseDatabase.getInstance().getReference("properties")
                            db.child(propertyId).setValue(property)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Property added successfully!", Toast.LENGTH_SHORT).show()
                                    (context as? Activity)?.finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to save: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.Green),
                    contentColor = Color.White // text/icon color
                ),
//                shape = RoundedCornerShape(12.dp) // optional styling
            ) {
                Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddPropertyPreview() {
    MaterialTheme {
        AddProperty()
    }
}

//in this screen , give an option to select the property location on the google maps
//and also give option to select the property image from gallery and upload the image to imgbb and save the url in firebase realtime database
//
//give the complte code