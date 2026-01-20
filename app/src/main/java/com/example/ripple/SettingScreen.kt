package com.example.ripple

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ripple.model.UserModel
import com.example.ripple.repository.UserRepoImpl
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen() {
    val context = LocalContext.current

    // Get current Firebase user
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userRepo = remember { UserRepoImpl() }

    // Compose states for user fields
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") } // old password needed for reauth
    var newPassword by remember { mutableStateOf("") } // if changing password
    var selectedDate by remember { mutableStateOf("") }

    // Load user data once
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userRepo.getUserById(userId) { success, _, user ->
                if (success && user != null) {
                    firstName = user.firstName
                    middleName = user.middleName ?: ""
                    lastName = user.lastName
                    username = user.username ?: ""
                    email = user.email
                    selectedDate = user.dob ?: ""
                }
            }
        }
    }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = "$year/${month + 1}/$day"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit Profile", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = middleName,
                onValueChange = { middleName = it },
                label = { Text("Middle Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Current Password (for re-auth)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password (leave blank if not changing)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { datePicker.show() }) {
                Text(text = if (selectedDate.isEmpty()) "Select Date" else selectedDate)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // 1. Update name, username, dob in Realtime DB
                    val updatedUser = UserModel(
                        userId = userId,
                        firstName = firstName,
                        middleName = middleName,
                        lastName = lastName,
                        username = username,
                        email = email,
                        dob = selectedDate
                    )

                    userRepo.editProfile(userId, updatedUser) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }

                    // 2. Update email if changed
                    if (email != FirebaseAuth.getInstance().currentUser?.email) {
                        userRepo.updateEmailWithReauth(
                            oldEmail = FirebaseAuth.getInstance().currentUser?.email ?: "",
                            oldPassword = password,
                            newEmail = email
                        ) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 3. Update password if newPassword is not empty
                    if (newPassword.isNotBlank()) {
                        userRepo.updatePasswordWithReauth(
                            oldEmail = FirebaseAuth.getInstance().currentUser?.email ?: "",
                            oldPassword = password,
                            newPassword = newPassword
                        ) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}

