package com.example.ripple

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.model.UserModel
import com.example.ripple.repository.UserRepoImpl
import com.example.ripple.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    userViewModel: UserViewModel = viewModel()
) {
    val uiState = userViewModel.uiState
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        userViewModel.updatePhoto(uri)
    }

    // Get current Firebase user
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userRepo = remember { UserRepoImpl() }

    // Compose states for user fields
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var originalEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    // UI states
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    originalEmail = user.email
                    selectedDate = user.dob ?: ""
                    userViewModel.updatePhoto(user.photoUrl?.let { Uri.parse(it) })

                }
            }
        }
    }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Circular profile photo widget
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(140.dp)
            ) {

                Image(
                    painter = rememberAsyncImagePainter(
                        model = if (!uiState.photoUrl.isNullOrEmpty())
                            uiState.photoUrl
                        else
                            R.drawable.circle_regular_full
                    ),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(2.dp, Color.Gray, CircleShape)
                )

                // Camera Icon
                IconButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.dp, Color.Gray, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Add Photo",
                        tint = Color.Black
                    )
                }

                // Remove photo icon
                if (uiState.photoUri != null || uiState.photoUrl.isNotEmpty()) {
                    IconButton(
                        onClick = { userViewModel.updatePhoto(null) }, // <- pass null to remove photo
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Photo",
                            tint = Color.Red
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Edit Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Profile fields ---
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = middleName,
                onValueChange = { middleName = it },
                label = { Text("Middle Name") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Current Password (for re-auth)") },
                enabled = !isLoading,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password (leave blank if not changing)") },
                enabled = !isLoading,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { datePicker.show() },
                enabled = !isLoading
            ) {
                Text(text = if (selectedDate.isEmpty()) "Select Date of Birth" else selectedDate)
            }

            // Error message display
            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    errorMessage = null

                    if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "Invalid email format"
                        return@Button
                    }

                    if (newPassword.isNotBlank() && newPassword.length < 6) {
                        errorMessage = "New password must be at least 6 characters"
                        return@Button
                    }

                    isLoading = true

                    scope.launch {
                        try {
                            val updatedUser = UserModel(
                                userId = userId,
                                firstName = firstName,
                                middleName = middleName,
                                lastName = lastName,
                                username = username,
                                email = email,
                                dob = selectedDate,
                                photoUrl = uiState.photoUrl
                            )

                            val profileResult = suspendCoroutine { cont ->
                                userRepo.editProfile(userId, updatedUser) { success, message ->
                                    cont.resume(success to message)
                                }
                            }

                            if (!profileResult.first) {
                                errorMessage = "Profile update failed: ${profileResult.second}"
                                isLoading = false
                                return@launch
                            }

                            if (email != originalEmail) {
                                val emailResult = suspendCoroutine { cont ->
                                    userRepo.updateEmailWithReauth(
                                        oldEmail = originalEmail,
                                        oldPassword = password,
                                        newEmail = email
                                    ) { success, message ->
                                        cont.resume(success to message)
                                    }
                                }

                                if (!emailResult.first) {
                                    errorMessage = "Email update failed: ${emailResult.second}"
                                    isLoading = false
                                    return@launch
                                }
                                originalEmail = email
                            }

                            if (newPassword.isNotBlank()) {
                                val passwordResult = suspendCoroutine { cont ->
                                    userRepo.updatePasswordWithReauth(
                                        oldEmail = originalEmail,
                                        oldPassword = password,
                                        newPassword = newPassword
                                    ) { success, message ->
                                        cont.resume(success to message)
                                    }
                                }

                                if (!passwordResult.first) {
                                    errorMessage = "Password update failed: ${passwordResult.second}"
                                    isLoading = false
                                    return@launch
                                }

                                val dbPasswordResult = suspendCoroutine { cont ->
                                    userRepo.updatePasswordInDatabase(userId, newPassword) { success, message ->
                                        cont.resume(success to message)
                                    }
                                }

                                if (!dbPasswordResult.first) {
                                    errorMessage = "Password saved to auth but database update failed: ${dbPasswordResult.second}"
                                }

                                password = ""
                                newPassword = ""
                            }

                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            errorMessage = "An error occurred: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}
