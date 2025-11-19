package jayanthkumar.project.realestateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen()
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    )
    {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.Brown))
                .padding(vertical = 6.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {

            Text(
                text = "Real Estate App",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.ic_realestate),
                contentDescription = "Real Estate App",
                modifier = Modifier
                    .size(44.dp)
                    .padding(start = 8.dp)
            )



        }


        Card(
            modifier = Modifier
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        )
        {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    painter = painterResource(id = R.drawable.image_one),

                    contentDescription = "Place Image",
                    contentScale = ContentScale.FillBounds
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The Green House",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 12.dp)

                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        modifier = Modifier
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = "RealEstate"
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Location",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier

                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        modifier = Modifier
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.area),
                        contentDescription = "Opening Hours Icon"
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Area",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier

                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        modifier = Modifier
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.price),
                        contentDescription = "Opening Hours Icon"
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Price",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier

                    )
                }

                Spacer(modifier = Modifier.height(16.dp))



            }
        }


        Card(
            modifier = Modifier
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        )
        {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    painter = painterResource(id = R.drawable.image_one),

                    contentDescription = "Place Image",
                    contentScale = ContentScale.FillBounds
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The Green House",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 12.dp)

                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        modifier = Modifier
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = ""
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Location",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier

                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        modifier = Modifier
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.area),
                        contentDescription = "Opening Hours Icon"
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Area",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier

                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        modifier = Modifier
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.price),
                        contentDescription = "Opening Hours Icon"
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Price",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier

                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


            }
        }
        
    }

}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}