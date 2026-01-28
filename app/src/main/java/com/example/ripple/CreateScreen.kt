package com.example.ripple

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ripple.viewmodel.UserViewModel
import java.io.File

/* ---------------------------------------------------
   CREATE SCREEN
---------------------------------------------------- */

@Composable
fun CreateScreen(
    userViewModel: UserViewModel = viewModel()
) {
    var showPopup by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Create Screen",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Popup
        CreateProfilePopup(
            userViewModel = userViewModel,
            showDialog = showPopup,
            onDismiss = { showPopup = false }
        )
    }
}

/* ---------------------------------------------------
   CREATE PROFILE POPUP
---------------------------------------------------- */

@Composable
fun CreateProfilePopup(
    userViewModel: UserViewModel,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    var step by remember { mutableStateOf(1) }

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

                // ---------------- STEP 1 ----------------
                if (step == 1) {
                    StepOnePhotoIntro(
                        userViewModel = userViewModel
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { step = 2 }
                    ) {
                        Text("Next")
                    }
                }

                // ---------------- STEP 2 ----------------
                if (step == 2) {
                    EditProfileContent(
                        userViewModel = userViewModel,
                        onClose = onDismiss
                    )
                }
            }
        }
    )
}

/* ---------------------------------------------------
   STEP 1 – PHOTO INTRO
---------------------------------------------------- */

@Composable
private fun StepOnePhotoIntro(
    userViewModel: UserViewModel
) {
    val uiState = userViewModel.uiState
    val context = LocalContext.current

    val savedPath = getSavedProfileImage(context)
    LaunchedEffect(savedPath) {
        if (savedPath != null) {
            userViewModel.uiState = userViewModel.uiState.copy(
                localPhotoPath = savedPath,
                photoUri = Uri.fromFile(File(savedPath))
            )
        }
    }



    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val localPath = saveImageToLocalStorage(context, it)

            // Update UI state immediately via ViewModel
            userViewModel.setLocalPhoto(localPath)

            // Upload to Firebase asynchronously
            userViewModel.updatePhoto(it)
        }
    }

    val imageModel: Any = when {
        uiState.localPhotoPath != null && File(uiState.localPhotoPath!!).exists() ->
            Uri.fromFile(File(uiState.localPhotoPath!!))
        uiState.photoUri != null -> uiState.photoUri
        uiState.photoUrl.isNotEmpty() -> uiState.photoUrl
        else -> R.drawable.circle_regular_full
    }


    // Circular profile photo widget
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(140.dp)

    ) {
        // Force reload by pairing with imageVersion
        val imageModelWithVersion: Any = when {
            uiState.localPhotoPath != null && File(uiState.localPhotoPath!!).exists() ->
                Uri.fromFile(File(uiState.localPhotoPath!!)).toString() + "?v=${uiState.imageVersion}"
            uiState.photoUri != null ->
                uiState.photoUri.toString() + "?v=${uiState.imageVersion}"
            uiState.photoUrl.isNotEmpty() ->
                uiState.photoUrl + "?v=${uiState.imageVersion}"
            else -> R.drawable.circle_regular_full
        }

        val painter = rememberAsyncImagePainter(model = imageModelWithVersion)

        Image(
            painter = painter,
            contentDescription = "Profile Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
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
        if (uiState.localPhotoPath != null || uiState.photoUri != null || uiState.photoUrl.isNotEmpty()) {
            IconButton(
                onClick = {
                    // Delete local file if exists
                    uiState.localPhotoPath?.let { File(it).delete() }
                    // Clear UI state
                    userViewModel.uiState = userViewModel.uiState.copy(
                        photoUri = null,
                        localPhotoPath = null,
                        photoUrl = ""
                    )
                    clearSavedProfileImage(context) // remove from SharedPreferences
                },
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

    Spacer(Modifier.height(24.dp))

    Text(
        text = "Create Your Profile",
        style = MaterialTheme.typography.headlineSmall
    )

    Spacer(Modifier.height(8.dp))

    Text(
        text = "Add a profile picture to get started",
        color = Color.Gray,
        textAlign = TextAlign.Center
    )
}
