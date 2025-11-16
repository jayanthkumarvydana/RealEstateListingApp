package jayanthkumar.project.realestateapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginActivityScreen()
        }
    }
}


@Composable
fun LoginActivityScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }


    val context = LocalContext.current as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.Green)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            text = "Real Estate App",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Text(
            text = "by Jayanth Kumar",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))


        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(26.dp)),

            ) {

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter Email") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
                shape = RoundedCornerShape(32.dp),
                leadingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Default.Email, // Replace with desired icon
                            contentDescription = "Email Icon"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Spacer(
                            modifier = Modifier
                                .width(3.dp) // Width of the line
                                .height(24.dp) // Adjust height as needed
                                .background(Color.Gray) // Color of the line
                        )
                    }
                },
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
                shape = RoundedCornerShape(32.dp),
                leadingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Default.Lock, // Replace with desired icon
                            contentDescription = "Email Password"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Spacer(
                            modifier = Modifier
                                .width(3.dp) // Width of the line
                                .height(24.dp) // Adjust height as needed
                                .background(Color.Gray) // Color of the line
                        )
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(46.dp))

            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            errorMessage = "Please enter your email."
                        }

                        password.isBlank() -> {
                            errorMessage = "Please enter your password."
                        }


                        else -> {
                            errorMessage = ""
                            val userData = UserData(
                                email = email,
                                password = password
                            )
                            fetchUserAccount(userData.email, userData.password, context)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.Brown),
                    contentColor = colorResource(
                        id = R.color.white
                    )
                ),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                )
            }

        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Lost Password ?", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Reset Now",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, // Blue text color for "Sign Up"
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, ResetPasswordActivity::class.java))
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "I'm new to this app !", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Join Now",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, // Blue text color for "Sign Up"
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, RegistrationActivity::class.java))
                    context.finish()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))


    }
}


@Preview(showBackground = true)
@Composable
fun LoginActivityScreenPreview() {
    LoginActivityScreen()
}


fun fetchUserAccount(userEmail: String, userPassword: String, context: Context) {
    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference

    val sanitizedEmail = userEmail.replace(".", ",")

    databaseReference.child("UserData").child(sanitizedEmail).get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userData = snapshot.getValue(UserData::class.java)
                userData?.let {

                    if (userPassword == it.password) {
                        Toast.makeText(context, "Login Successfull", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Incorrect Credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "No User Found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            println("Error retrieving data: ${exception.message}")
        }
}

