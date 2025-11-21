package jayanthkumar.project.realestateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.painterResource


class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageCardWithActions("https://cms.ezylegal.in/wp-content/uploads/2022/04/types-of-properties-1.jpg","Ananada Niketan")
        }
    }
}


@Composable
fun ImageCardWithActions(
    imageUrl: String,
    title: String,
    onBackClick: () -> Unit = {},
    onFavClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    var selectedChip by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Image
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // --- TOP BAR (Back icon left, Fav + Dots right) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
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

                // Right → Fav + 3 dots
                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(onClick = onFavClick) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onMoreClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }
                }
            }

            // --- BOTTOM TITLE OVERLAY ---
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                    .background(
                        Color.White.copy(alpha = 0.85f) // soft white overlay
                    )
                    .padding(12.dp)
            ) {
                Column() {

                    Row()
                    {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "$350,00",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Looking for a home that's modern, elegant, and peaceful? Ananda Niketan has the perfect match for you-whether it's your dream residence or a smart investment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FeatureChip(
                            label = "384 Bagbari",
                            icon = Icons.Default.LocationOn,
                            isSelected = selectedChip == "384 Bagbari"
                        ) { selectedChip = it }

                        FeatureChip(
                            label = "Parking",
                            icon = Icons.Default.LocalParking,
                            isSelected = selectedChip == "Parking"
                        ) { selectedChip = it }

                        FeatureChip(
                            label = "Swimming Pool",
                            icon = Icons.Default.Pool,
                            isSelected = selectedChip == "Swimming Pool"
                        ) { selectedChip = it }
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
                                text = "1100 sqft",
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
                                text = "1 Baths",
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
                                text = "Villa",
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
                        onClick = { },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, Color.Gray),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Curved Border Button")
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
    ImageCardWithActions("https://cms.ezylegal.in/wp-content/uploads/2022/04/types-of-properties-1.jpg","Ananada Niketan")
}