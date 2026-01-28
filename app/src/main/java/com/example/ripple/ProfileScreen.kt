package com.example.ripple

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.viewmodel.UserViewModel
import java.io.File

/* ---------------------------------------------------
   PROFILE SCREEN
---------------------------------------------------- */

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = viewModel()
) {
    val uiState = userViewModel.uiState
    val context = LocalContext.current

    // 🔑 Controls CreateScreen popup visibility
    var showCreatePopup by remember { mutableStateOf(true) }

    // Reload user when screen opens
    LaunchedEffect(Unit) {
        userViewModel.loadUser()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ================= PROFILE CONTENT =================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileHeaderSection(userViewModel)

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ProfileFieldItem("First name", uiState.firstName ?: "")
                    ProfileFieldItem("Middle name", uiState.middleName ?: "")
                    ProfileFieldItem("Last name", uiState.lastName ?: "")
                    ProfileFieldItem("Username", uiState.username)
                    ProfileFieldItem("Date of birth", uiState.dob ?: "")
                }
            }
        }

        // ================= CREATE PROFILE POPUP =================
        CreateProfilePopup(
            userViewModel = userViewModel,
            showDialog = showCreatePopup,
            onDismiss = {
                showCreatePopup = false
                userViewModel.loadUser()
            }
        )
    }
}

/* ---------------------------------------------------
   PROFILE HEADER
---------------------------------------------------- */

@Composable
private fun ProfileHeaderSection(
    userViewModel: UserViewModel
) {
    val uiState = userViewModel.uiState
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val localPath = saveImageToLocalStorage(context, it)
            userViewModel.setLocalPhoto(localPath)
            userViewModel.updatePhoto(it)
        }
    }

    val imageModelWithVersion: Any = when {
        uiState.localPhotoPath != null && File(uiState.localPhotoPath!!).exists() ->
            Uri.fromFile(File(uiState.localPhotoPath!!)).toString() + "?v=${uiState.imageVersion}"
        uiState.photoUri != null ->
            uiState.photoUri.toString() + "?v=${uiState.imageVersion}"
        uiState.photoUrl.isNotEmpty() ->
            uiState.photoUrl + "?v=${uiState.imageVersion}"
        else -> R.drawable.circle_regular_full
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(140.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageModelWithVersion),
            contentDescription = "Profile Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
        )

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
                contentDescription = "Change Photo",
                tint = Color.Black
            )
        }

        if (uiState.localPhotoPath != null || uiState.photoUri != null || uiState.photoUrl.isNotEmpty()) {
            IconButton(
                onClick = {
                    uiState.localPhotoPath?.let { File(it).delete() }
                    userViewModel.uiState = userViewModel.uiState.copy(
                        photoUri = null,
                        localPhotoPath = null,
                        photoUrl = ""
                    )
                    clearSavedProfileImage(context)
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

    Spacer(modifier = Modifier.height(16.dp))

    Text(uiState.username.ifEmpty { "User" }, fontSize = 20.sp)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Personal profile", fontSize = 14.sp, color = Color.Gray)
}

/* ---------------------------------------------------
   PROFILE FIELD ITEM
---------------------------------------------------- */

@Composable
private fun ProfileFieldItem(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value.ifEmpty { "—" }, fontSize = 16.sp, color = Color.Black)
    }
}
