package com.example.ripple

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.viewmodel.UserViewModel
import androidx.compose.foundation.border


@Composable
fun CreateScreen(
    userViewModel: UserViewModel = viewModel()
) {
    val uiState = userViewModel.uiState
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        userViewModel.updatePhoto(uri)
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(140.dp)
        ) {

            Image(
                painter = rememberAsyncImagePainter(
                    model = uiState.photoUri ?: uiState.photoUrl.ifEmpty { R.drawable.circle_regular_full }
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

            // Remove photo icon (optional)
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
            text = "Create Your Profile",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Add a profile picture to get started",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Example: Other buttons or content
        Text(
            text = "Next",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .background(Color(0xFF6200EE), shape = CircleShape)
                .clickable { /* Handle click */ }
                .padding(vertical = 12.dp),
            color = Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
