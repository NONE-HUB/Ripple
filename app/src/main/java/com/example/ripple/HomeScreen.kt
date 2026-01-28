package com.example.ripple

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.model.Posted
import com.example.ripple.repository.PostRepoImpl
import com.example.ripple.viewmodel.UserUiState
import com.example.ripple.viewmodel.UserViewModel
import java.io.File
import java.util.*

@Composable
fun HomeScreen(userViewModel: UserViewModel) {
    val context = LocalContext.current
    val postRepo = PostRepoImpl()
    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    var posts by remember { mutableStateOf(listOf<Posted>()) }
    var showSuccess by remember { mutableStateOf(false) }

    // Load all posts from Firebase on start
    LaunchedEffect(Unit) {
        postRepo.getAllPosts { data ->
            posts = data
        }
    }


    // Image picker launcher
    val imagePicker: ManagedActivityResultLauncher<String, Uri?> =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    Box(modifier = Modifier.fillMaxSize()) {

        // Posts list
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posts.reversed()) { post ->
                PostCardWithProfile(
                    userViewModel = userViewModel,
                    uiState = UserUiState(
                        photoUrl = post.userPhotoUrl ?: "",
                        photoUri = null,
                        username = post.userName ?: ""
                    ),
                    description = post.description,
                    imagePath = post.imageLocalPath
                )
            }
        }

        // Floating add button
        Icon(
            painter = rememberAsyncImagePainter(R.drawable.create),
            contentDescription = "Add Post",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp)
                .clickable { showDialog = true }
        )
    }

    // Add post dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    if (userId.isEmpty()) return@TextButton

                    // Save image locally
                    var localPath: String? = null
                    selectedImageUri?.let { uri ->
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val file = File(context.filesDir, "${UUID.randomUUID()}.jpg")
                        inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                        localPath = file.absolutePath
                    }

                    val post = Posted(
                        userId = userId,
                        description = description,
                        imageLocalPath = localPath,
                        userPhotoUrl = userViewModel.uiState.photoUrl,
                        userName = userViewModel.uiState.username
                    )


                    postRepo.addPost(post) { success, msg ->
                        if (success) {
                            posts = posts + post
                            showSuccess = true
                        }
                        showDialog = false
                        selectedImageUri = null
                        description = ""
                    }
                }) {
                    Text("Post")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    selectedImageUri = null
                    description = ""
                }) {
                    Text("Cancel")
                }
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            val bitmap = context.contentResolver.openInputStream(selectedImageUri!!)?.use { input ->
                                BitmapFactory.decodeStream(input)
                            }
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Text("Click to select image")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showSuccess) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { showSuccess = false }) {
                    Text("OK", color = Color.White)
                }
            }
        ) {
            Text("Post added successfully!", color = Color.White)
        }
    }
}

@Composable
fun PostCardWithProfile(
    userViewModel: UserViewModel,
    uiState: UserUiState,
    description: String,
    imagePath: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular profile photo
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(80.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = uiState.photoUrl.ifEmpty { R.drawable.circle_regular_full }
                ),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description on top
        Text(text = description, fontSize = 16.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))

        // Image below description
        imagePath?.let { path ->
            val bitmap = BitmapFactory.decodeFile(path)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
