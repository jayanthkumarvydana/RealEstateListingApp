package jayanthkumar.project.realestateapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import jayanthkumar.project.realestateapp.selectedProperty.property
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PropertyDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PropertyDetailsScreen(
                selectedProperty.property, onFavClick = {
                    val localDb = AppDatabase.getDatabase(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        localDb.propertyDao().insertProperty(property.toEntity())
                    }

                    Toast.makeText(this, "Property saved locally", Toast.LENGTH_SHORT).show()

                }, onBackClick = { (this as Activity).finish() },
                onMoreClick = {
                    val intent = Intent(this, MapPickerActivity::class.java)
                    startActivity(intent)
                })
        }
    }
}

@Composable
fun PropertyDetailsAppBar(
    onBackClick: () -> Unit = {},
    isFavorite: Boolean,
    onFavoriteToggle: (Boolean) -> Unit,
    db: AppDatabase,
    scope: CoroutineScope,
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.Brown))
            .padding(start = 10.dp, end = 10.dp, top = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Left → Back Icon
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Text(
            "Property Details",
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Color.White
        )

        // Right → Fav + 3 dots
        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        if (isFavorite) {
                            //  Remove from DB
                            db.propertyDao().deletePropertyById(property.id)
                        } else {
                            // Save to DB
                            val entity = PropertyEntity(
                                id = property.id,
                                propertyName = property.propertyName,
                                contact = property.contact,
                                description = property.description,
                                price = property.price,
                                place = property.place,
                                beds = property.beds,
                                baths = property.baths,
                                area = property.area,
                                type = property.type,
                                amenities = property.amenities.joinToString(", ")
                            )
                            db.propertyDao().insertProperty(entity)
                        }

                        // Toggle color on main thread
                        onFavoriteToggle(!isFavorite)
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }


            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(

    property: Property,
    onBackClick: () -> Unit = {},
    onFavClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    var selectedChip by remember { mutableStateOf<String?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()


    // 🔹 Check if property is already saved in Room
    LaunchedEffect(property.id) {
        scope.launch(Dispatchers.IO) {
            val existing = db.propertyDao().getPropertyById(property.id)
            isFavorite = existing != null
        }
    }


    Scaffold(
        topBar = {
            PropertyDetailsAppBar(
                onBackClick = onBackClick,
                isFavorite = isFavorite,
                onFavoriteToggle = { newValue ->
                    isFavorite = newValue
                },
                db = db,
                scope = scope,
                onMoreClick = onMoreClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {

                if (property.imageUrl.isEmpty()) {
                    AsyncImage(
                        model = "https://cms.ezylegal.in/wp-content/uploads/2022/04/types-of-properties-1.jpg",
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()

                    )
                } else {
                    AsyncImage(
                        model = property.imageUrl,
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()

                    )
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.85f)
                    )
                    .padding(12.dp)
            ) {
                Column() {

                    Row()
                    {
                        Text(
                            text = property.propertyName,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "£${property.price}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = property.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FeatureChip(
                            label = property.place,
                            icon = Icons.Default.LocationOn,
                            isSelected = selectedChip == property.place
                        ) { selectedChip = it }

                        Spacer(modifier = Modifier.width(12.dp))

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            property.amenities.forEach { amenity ->
                                FeatureChip(
                                    label = amenity,
                                    icon = Icons.Default.Circle,
                                    isSelected = selectedChip == property.place
                                ) { selectedChip = it }
                            }
                        }

//                        FeatureChip(
//                            label = "Parking",
//                            icon = Icons.Default.LocalParking,
//                            isSelected = selectedChip == "Parking"
//                        ) { selectedChip = it }
//
//                        FeatureChip(
//                            label = "Swimming Pool",
//                            icon = Icons.Default.Pool,
//                            isSelected = selectedChip == "Swimming Pool"
//                        ) { selectedChip = it }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    )
                    {
                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {

                                }
                                .background(
                                    color = Color(0xFFEFEFEF),   // your background color
                                    shape = RoundedCornerShape(12.dp) // optional rounded corners
                                )
                                .padding(16.dp) // inner padding so content doesn't touch edges
                        )
                        {

                            Image(
                                painter = painterResource(id = R.drawable.area_2),
                                contentDescription = "Real Estate",
                                modifier = Modifier
                                    .size(18.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = property.area,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Area",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black

                            )

                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = Color(0xFFEFEFEF),   // your background color
                                    shape = RoundedCornerShape(12.dp) // optional rounded corners
                                )
                                .padding(16.dp) // inner padding so content doesn't touch edges

                        )
                        {

                            Image(
                                painter = painterResource(id = R.drawable.bath),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(18.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = property.baths,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold

                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Bedrooms",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black

                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = Color(0xFFEFEFEF),   // your background color
                                    shape = RoundedCornerShape(12.dp) // optional rounded corners
                                )
                                .padding(16.dp) // inner padding so content doesn't touch edges

                        )
                        {

                            Image(
                                painter = painterResource(id = R.drawable.villa),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(18.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = property.type,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,

                                )
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Type",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black,


                                )


                        }


                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${property.contact}")
                            context.startActivity(intent)

                        },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, Color.Gray),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Call Owner")
                    }


                }

            }


        }
    }
}


@Composable
fun FeatureChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color.LightGray else Color.White)
            .border(
                1.dp,
                Color.Gray,
                RoundedCornerShape(20.dp)
            )
            .clickable { onSelect(label) }
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PropertyDetailsPreview() {
    val dummyProperty = Property(
        id = "P001",
        propertyName = "Ananda Niketan",
        contact = "9876543210",
        description = "A luxurious 3BHK villa located in the heart of Bangalore. Features a private garden, modular kitchen, and modern interiors with great connectivity.",
        price = "85,00,000",
        place = "Bangalore",
        beds = "3",
        baths = "2",
        area = "1650 sqft",
        type = "Villa",
        amenities = listOf("Wi-Fi", "Parking", "Swimming Pool", "Gym", "Garden")
    )
    PropertyDetailsScreen(property = dummyProperty)
}