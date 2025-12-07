package jayanthkumar.project.realestateapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedPropertiesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SavedPropertiesScreen(onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun SavePropertyCard(
    property: Property,
    onDeleteClick: (Property) -> Unit = {} // 👈 Added callback for delete action
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectedProperty.property = property
                val intent = Intent(context, PropertyDetails::class.java)
                context.startActivity(intent)
            }
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 🖼 Image
            Box {
                Image(
                    painter = painterResource(id = R.drawable.image_one),
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                // 🔹 Overlay Top: Tag + Delete Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = property.type.ifEmpty { "For Sale" },
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // 🗑 Delete Button instead of Favorite
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    ) {
                        IconButton(onClick = { onDeleteClick(property) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Property",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 Title & Price
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = property.propertyName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    text = "£${property.price}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 🔹 Info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SaveInfoChip(Icons.Default.Place, property.place)
                SaveInfoChip(Icons.Default.Bed, "${property.beds} Beds")
                SaveInfoChip(Icons.Default.Bathtub, "${property.baths} Baths")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}





@Composable
fun SaveInfoChip(icon: ImageVector, text: String) {
    Surface(
        color = Color(0xFFF1F3F6),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPropertiesScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    var propertyList by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Function to reload data
    fun reloadProperties() {
        scope.launch(Dispatchers.IO) {
            val entities = db.propertyDao().getAllProperties()
            propertyList = entities.map {
                Property(
                    id = it.id,
                    propertyName = it.propertyName,
                    contact = it.contact,
                    description = it.description,
                    price = it.price,
                    place = it.place,
                    beds = it.beds,
                    baths = it.baths,
                    area = it.area,
                    type = it.type,
                    amenities = it.amenities.split(",").map { amenity -> amenity.trim() }
                )
            }
            isLoading = false
        }
    }

    // ✅ Initial Load
    LaunchedEffect(Unit) {
        reloadProperties()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Saved Properties",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.Brown)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                propertyList.isEmpty() -> {
                    Text(
                        text = "No saved properties found.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(propertyList) { property ->
                            SavePropertyCard(
                                property = property,
                                onDeleteClick = {
                                    scope.launch(Dispatchers.IO) {
                                        db.propertyDao().deletePropertyById(property.id)
                                        reloadProperties() // ✅ Reload UI immediately after delete
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
