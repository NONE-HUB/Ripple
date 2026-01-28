package com.example.ripple

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.File

/* ---------- Design Tokens ---------- */
private val GradientTop = Color(0xFFF3F6FB)
private val GradientBottom = Color.White
private val Surface = Color.White
private val PrimaryText = Color(0xFF111827)
private val SecondaryText = Color(0xFF6B7280)
private val BorderSubtle = Color(0xFFE5E7EB)
private val SkeletonBase = Color(0xFFE5E7EB)
private val SkeletonHighlight = Color(0xFFF3F4F6)

@Composable
fun ProfileScreen(userViewModel: UserViewModel = viewModel()) {
//    val uiState by remember { derivedStateOf { userViewModel.uiState } } // Compose reactive state
    val uiState = userViewModel.uiState
    val context = LocalContext.current



    // Date picker
    val calendar = java.util.Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            userViewModel.updateDob(String.format("%04d-%02d-%02d", year, month + 1, day))
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )

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



    // Reload user whenever screen opens
    LaunchedEffect(Unit) {
        userViewModel.loadUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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

        Spacer(modifier = Modifier.height(16.dp))

        Text(uiState.username.ifEmpty { "User" }, fontSize = 20.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Personal profile", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // Profile info card
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
}

/* -----------------------------------
   Header with Edit Photo
----------------------------------- */
@Composable
private fun ProfileHeader(
    uiState: com.example.ripple.viewmodel.UserUiState,
    imagePicker: androidx.activity.result.ActivityResultLauncher<String>,
    userViewModel: UserViewModel
) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(124.dp)
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = uiState.photoUri ?: uiState.photoUrl.ifEmpty { R.drawable.circle_regular_full }
            ),
            contentDescription = "Profile Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(124.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .border(1.dp, BorderSubtle, CircleShape)
        )

    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = uiState.username.ifEmpty { "User" },
        fontSize = 20.sp,
        color = PrimaryText
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Personal profile",
        fontSize = 14.sp,
        color = SecondaryText
    )
}

/* -----------------------------------
   Profile Card
----------------------------------- */
@Composable
private fun ProfileCard(uiState: com.example.ripple.viewmodel.UserUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(3.dp)
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

/* -----------------------------------
   Field Item
----------------------------------- */
@Composable
private fun ProfileFieldItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value.ifEmpty { "—" }, fontSize = 16.sp, color = Color.Black)
    }
}

/* -----------------------------------
   Skeletons
----------------------------------- */
@Composable
private fun SkeletonProfileHeader() {
    ShimmerCircle(124.dp)
    Spacer(modifier = Modifier.height(16.dp))
    ShimmerLine(120.dp)
    Spacer(modifier = Modifier.height(8.dp))
    ShimmerLine(90.dp)
}

@Composable
private fun SkeletonCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            repeat(5) {
                ShimmerLine()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/* -----------------------------------
   Shimmer Helpers
----------------------------------- */
@Composable
private fun ShimmerLine(width: Dp = Dp.Unspecified) {
    Box(
        modifier = Modifier
            .height(16.dp)
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .clip(RoundedCornerShape(8.dp))
            .background(shimmerBrush())
    )
}

@Composable
private fun ShimmerCircle(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush())
    )
}

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition()
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    return Brush.linearGradient(
        colors = listOf(SkeletonBase, SkeletonHighlight, SkeletonBase),
        start = androidx.compose.ui.geometry.Offset(translate - 200, 0f),
        end = androidx.compose.ui.geometry.Offset(translate, 0f)
    )
}
