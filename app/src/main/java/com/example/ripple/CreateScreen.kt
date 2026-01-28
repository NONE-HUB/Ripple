package com.example.ripple

import UserModel
import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.repository.UserRepoImpl
import com.example.ripple.viewmodel.UserViewModel
import java.util.*
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume


@Composable
fun CreateScreen(userViewModel: UserViewModel = viewModel()) {
    // Show popup immediately
    var showPopup by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Optional background content behind the popup
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to Create Screen", style = MaterialTheme.typography.titleLarge)
        }

        // Show popup
        CreateProfilePopup(
            userViewModel = userViewModel,
            showDialog = showPopup,
            onDismiss = { showPopup = false }
        )
    }
}

@Composable
fun CreateProfilePopup(
    userViewModel: UserViewModel,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    val uiState = userViewModel.uiState
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Coroutine scope for backend
    val userRepo = remember { UserRepoImpl() } // Backend repo (same as SettingsScreen)

    var step by remember { mutableStateOf(1) }

    var firstName by remember { mutableStateOf(uiState.firstName ?: "") }
    var middleName by remember { mutableStateOf(uiState.middleName ?: "") }
    var lastName by remember { mutableStateOf(uiState.lastName ?: "") }
    var dob by remember { mutableStateOf(uiState.dob ?: "") }
    var username by remember { mutableStateOf(uiState.username ?: "") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        userViewModel.updatePhoto(uri) // Backend: update photo
    }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dob = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        confirmButton = {},
        dismissButton = {},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ================= STEP 1 =================
                if (step == 1) {
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = uiState.photoUri ?: uiState.photoUrl.ifEmpty { R.drawable.circle_regular_full }
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(2.dp, Color.Gray, CircleShape)
                        )

                        IconButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(Icons.Default.AddAPhoto, null)
                        }

                        if (uiState.photoUri != null || uiState.photoUrl.isNotEmpty()) {
                            IconButton(
                                onClick = { userViewModel.updatePhoto(null) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Create Your Profile",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Add a profile picture to get started",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                // ================= STEP 2 =================
                if (step == 2) {
                    Text(
                        "Personal Information",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = middleName,
                        onValueChange = { middleName = it },
                        label = { Text("Middle Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { datePicker.show() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (dob.isEmpty()) "Select Date of Birth" else dob,
                            color = Color.White
                        )
                    }
                }

                // ================= STEP 3 =================
                if (step == 3) {
                    Text(
                        "Set Your Username",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(32.dp))

                // ================= NAV BUTTONS =================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Back button
                    if (step > 1) {
                        Button(
                            onClick = { step -= 1 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Back")
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }

                    Spacer(Modifier.width(12.dp))

                    // Next / Save button
                    Button(
                        onClick = {
                            when (step) {
                                1 -> step = 2
                                2 -> step = 3
                                3 -> {
                                    // ------------------- SAVE LOGIC (SettingsScreen backend) -------------------
                                    if (firstName.isBlank() || lastName.isBlank() || username.isBlank()) {
                                        errorMessage = "Please fill all required fields"
                                        return@Button
                                    }

                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val userId = userViewModel.uiState.userId
                                            if (userId.isNotEmpty()) {
                                                val updatedUser = UserModel(
                                                    userId = userId,
                                                    firstName = firstName,
                                                    middleName = middleName,
                                                    lastName = lastName,
                                                    username = username,
                                                    dob = dob,
                                                    photoUrl = userViewModel.uiState.photoUrl,
                                                    email = userViewModel.uiState.email
                                                )

                                                val profileResult = suspendCoroutine<Pair<Boolean, String>> { cont ->
                                                    userRepo.editProfile(userId, updatedUser) { success, message ->
                                                        cont.resume(success to message)
                                                    }
                                                }

                                                if (!profileResult.first) {
                                                    errorMessage = "Profile update failed: ${profileResult.second}"
                                                } else {
                                                    userViewModel.loadUser() // refresh UI immediately
                                                    onDismiss()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "An error occurred: ${e.message}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                    // ------------------- END SAVE LOGIC -------------------
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (step < 3) "Next" else "Save")
                    }
                }

                // Error message
                errorMessage?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
        }
    )
}

