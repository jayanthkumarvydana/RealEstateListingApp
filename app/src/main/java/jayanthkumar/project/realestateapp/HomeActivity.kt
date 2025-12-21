package jayanthkumar.project.realestateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MaterialTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    var propertyList by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showFilters by remember { mutableStateOf(false) }

    var selectedPlace by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") }
    var maxPrice by remember { mutableStateOf(10000f) }


    val database = FirebaseDatabase.getInstance().getReference("properties")

    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Property>()

                // 🔹 Loop through users
                for (userSnapshot in snapshot.children) {

                    // 🔹 Loop through properties of each user
                    for (propertySnapshot in userSnapshot.children) {
                        val property =
                            propertySnapshot.getValue(Property::class.java)

                        if (property != null) {
                            list.add(property)
                        }
                    }
                }

                propertyList = list
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
                isLoading = false
            }
        })
    }


    val filteredList = propertyList.filter { property ->

        val placeMatch =
            selectedPlace == "All" || property.place == selectedPlace

        val typeMatch =
            selectedType == "All" || property.type == selectedType

        val priceValue = property.price.toFloatOrNull() ?: 0f
        val priceMatch = priceValue <= maxPrice

        placeMatch && typeMatch && priceMatch
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to AddProperty screen
                    val intent = Intent(context, AddPropertyActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = colorResource(id = R.color.white), // ✅ Custom color from resources
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Property", tint = Color.Black)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.Brown))
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(36.dp)
                        .clickable {
                            val intent = Intent(context, ProfileActivity::class.java)
                            context.startActivity(intent)
                        }

                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Real Estate App",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.save),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            val intent = Intent(context, SavedPropertiesActivity::class.java)
                            context.startActivity(intent)
                        }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            PropertyPreviewRow()

            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(modifier= Modifier.fillMaxWidth()) {

                        Text(
                            "Filters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier= Modifier.weight(1f))

                        Text(
                            if(showFilters) "Hide" else "Show",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue,
                            modifier = Modifier.clickable { showFilters = !showFilters }
                        )

                    }


                    if(showFilters) {
                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                FilterDropdown(
                                    label = "Place",
                                    options = ukPlacesFilter(),
                                    selected = selectedPlace,
                                    onSelected = { selectedPlace = it }
                                )
                            }

                            Column(Modifier.weight(1f)) {
                                FilterDropdown(
                                    label = "Type",
                                    options = propertyTypesFilter(),
                                    selected = selectedType,
                                    onSelected = { selectedType = it }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Max Price: £${maxPrice.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Slider(
                            value = maxPrice,
                            onValueChange = { maxPrice = it },
                            valueRange = 500f..10000f,
                            steps = 18
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(6.dp))


            // Loading or Data
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No properties found.\nClick + to add one!",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {

                Text(
                    "Available Properties",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { property ->
                        PropertyCard(property)
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun PropertyPreviewRow() {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PropertyPreviewCard(
            data = PropertyCardUi(
                title = "My Listings",
                location = "Added by you",
                imageRes = R.drawable.realstate_icon,
                darkCard = true
            ),
            onAddClick = {
                val intent = Intent(context, MyPropertiesActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.weight(1f)
        )

        PropertyPreviewCard(
            data = PropertyCardUi(
                title = "Explore Map",
                location = "Properties on map",
                imageRes = R.drawable.ic_map,
                darkCard = false
            ),
            onAddClick = {
                val intent = Intent(context, PropertiesMapActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen()
    }
}


fun ukPlacesFilter() = listOf(
    "All",
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

fun propertyTypesFilter() = listOf(
    "All",
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