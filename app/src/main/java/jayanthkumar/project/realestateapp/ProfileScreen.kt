package jayanthkumar.project.realestateapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userEmail = UserPrefs.getEmail(this).replace(".", ",")
            ProfileScreen(userEmail, onBack = {
                finish()
            })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userKey: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    val database =
        FirebaseDatabase.getInstance()
            .getReference("UserData")
            .child(userKey)

    // ✅ FIX: Convert SharedPref values into Compose state
    var fullName by remember {
        mutableStateOf(UserPrefs.getName(context))
    }
    var email by remember {
        mutableStateOf(UserPrefs.getEmail(context))
    }
    var zipCode by remember {
        mutableStateOf(UserPrefs.getZipCode(context))
    }

    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.Brown) // ✅ use your custom color here
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔹 Full Name (editable)
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 🔹 Email (read-only)
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = false
            )

            // 🔹 Zip Code (editable)
            OutlinedTextField(
                value = zipCode,
                onValueChange = { zipCode = it },
                label = { Text("Zip Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || zipCode.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    isSaving = true

                    val updates = mapOf(
                        "fullName" to fullName,
                        "zipCode" to zipCode
                    )

                    database.updateChildren(updates)
                        .addOnSuccessListener {
                            UserPrefs.saveName(context, fullName)
                            UserPrefs.saveZipCode(context, zipCode)

                            Toast.makeText(
                                context,
                                "Profile updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            isSaving = false

                            (context as Activity).finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Update failed: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            isSaving = false
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.Brown),
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (isSaving) "Saving..." else "Update Profile",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


