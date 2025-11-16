package jayanthkumar.project.realestateapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordScreen()
        }
    }
}


@Composable
fun ResetPasswordScreen() {

    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var step2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val context = LocalContext.current as Activity


    val dbRef = FirebaseDatabase.getInstance().getReference("SignedUpUsers")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorResource(id = R.color.Green),
            ),
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Real Estate App",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Reset Password",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(26.dp)),

            ) {

            // STEP 1 -> EMAIL + DOB
            if (!step2) {

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

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (dd-mm-yyyy)") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                    shape = RoundedCornerShape(32.dp),
                    leadingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Default.DateRange, // Replace with desired icon
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

                Spacer(Modifier.height(20.dp))



                Button(
                    onClick = {
                        loading = true
                        errorMessage = ""
                        successMessage = ""

                        val key = email.replace(".", ",")

                        dbRef.child(key).get()
                            .addOnSuccessListener { snapshot ->
                                loading = false

                                if (!snapshot.exists()) {
                                    errorMessage = "User not found"
                                    return@addOnSuccessListener
                                }

                                val dbEmail = snapshot.child("email").value?.toString() ?: ""
                                val dbDob = snapshot.child("dob").value?.toString() ?: ""

                                if (dbEmail == email && dbDob == dob) {
                                    step2 = true // show new password fields
                                } else {
                                    errorMessage = "Email or DOB incorrect"
                                }
                            }
                            .addOnFailureListener {
                                loading = false
                                errorMessage = "Error: ${it.localizedMessage}"
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
                        text = "Verify",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )
                }

                // STEP 2 -> ENTER NEW PASSWORD
                if (step2) {

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
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
                                    contentDescription = "Lock Icon"
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

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
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
                                    contentDescription = "Lock Icon"
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

                    Spacer(Modifier.height(20.dp))



                    Button(
                        onClick = {
                            errorMessage = ""
                            successMessage = ""

                            if (newPassword != confirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@Button
                            }

                            loading = true

                            val key = email.replace(".", ",")

                            dbRef.child(key).child("password").setValue(newPassword)
                                .addOnSuccessListener {
                                    loading = false
                                    successMessage = "Password updated successfully!"

                                    context.startActivity(
                                        Intent(
                                            context,
                                            LoginActivity::class.java
                                        )
                                    )
                                    context.finish()
                                }
                                .addOnFailureListener {
                                    loading = false
                                    errorMessage = "Failed to update password"
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
                            text = "Update Password",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }

        }

        Spacer(Modifier.height(20.dp))

        if (loading) Text("Processing...")

        if (errorMessage.isNotEmpty())
            Text(errorMessage, color = MaterialTheme.colorScheme.error)

        if (successMessage.isNotEmpty())
            Text(successMessage, color = MaterialTheme.colorScheme.primary)
    }

}