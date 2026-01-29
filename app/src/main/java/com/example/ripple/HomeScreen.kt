package com.example.ripple

import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.material.icons.filled.Edit
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
import com.example.ripple.model.Comment
import com.example.ripple.model.Posted
import com.example.ripple.repository.PostRepoImpl
import com.example.ripple.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.*

@Composable
fun HomeScreen(userViewModel: UserViewModel) {

    val context = LocalContext.current
    val postRepo = remember { PostRepoImpl() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var posts by remember { mutableStateOf<List<Posted>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingPost by remember { mutableStateOf<Posted?>(null) }

    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Load posts
    LaunchedEffect(Unit) {
        postRepo.getAllPosts { posts = it }
    }

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            selectedImageUri = it
        }

    val actionSize = 36.dp


    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posts.reversed()) { post ->
                PostCard(
                    post = post,
                    isOwner = post.userId == userId,
                    onEdit = {
                        editingPost = it
                        description = it.description
                        showDialog = true
                    },
                    onDelete = {
                        postRepo.deletePost(it.postId) { success, _ ->
                            if (success) {
                                posts = posts.filterNot { p -> p.postId == it.postId }
                            }
                        }
                    }
                )
            }
        }

        // Add post button
        Icon(
            painter = rememberAsyncImagePainter(R.drawable.create),
            contentDescription = "Add Post",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp)
                .clickable {
                    editingPost = null
                    description = ""
                    selectedImageUri = null
                    showDialog = true
                }
        )
    }

    // Create / Edit Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {

                    var localPath: String? = editingPost?.imageLocalPath

                    selectedImageUri?.let { uri ->
                        val file = File(context.filesDir, "${UUID.randomUUID()}.jpg")
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            file.outputStream().use { output -> input.copyTo(output) }
                        }
                        localPath = file.absolutePath
                    }

                    if (editingPost != null) {
                        val updated = editingPost!!.copy(
                            description = description,
                            imageLocalPath = localPath
                        )

                        postRepo.updatePost(updated) { success, _ ->
                            if (success){
                            posts = posts.map {
                                if (it.postId == updated.postId) updated else it
                            }
                            }
                        }
                    } else {
                        val newPost = Posted(
                            postId = UUID.randomUUID().toString(),
                            userId = userId,
                            description = description,
                            imageLocalPath = localPath,
                            userPhotoUrl = userViewModel.uiState.photoUrl,
                            userName = userViewModel.uiState.username
                        )

                        postRepo.addPost(newPost) { success, _ ->
                            if (success) {
                                // reload from DB
                                postRepo.getAllPosts { posts = it }
                            }
                        }
                    }

                    showDialog = false
                    editingPost = null
                    description = ""
                    selectedImageUri = null

                }) {
                    Text(if (editingPost != null) "Update" else "Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
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
                        when {
                            selectedImageUri != null -> {
                                val bitmap = context.contentResolver
                                    .openInputStream(selectedImageUri!!)
                                    ?.use { BitmapFactory.decodeStream(it) }
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            editingPost?.imageLocalPath != null -> {
                                val bitmap = BitmapFactory.decodeFile(editingPost!!.imageLocalPath!!)
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            else -> Text("Select image")
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
            }
        )
    }
}

@Composable
fun PostCard(
    post: Posted,
    isOwner: Boolean,
    onEdit: (Posted) -> Unit,
    onDelete: (Posted) -> Unit
) {
    val actionSize = 36.dp

    // Comments state for this post
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var showCommentInput by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {

        val userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        val userName = userViewModel.uiState.username

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

        if (isOwner) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {



                // 🔵 LEFT circle (post owner photo)
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(actionSize)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageModelWithVersion),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                    )

//                    IconButton(
//                        onClick = { imagePicker.launch("image/*") },
//                        modifier = Modifier
//                            .size(40.dp)
//                            .clip(CircleShape)
//                            .background(Color.White.copy(alpha = 0.9f))
//                            .border(1.dp, Color.Gray, CircleShape)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.AddAPhoto,
//                            contentDescription = "Change Photo",
//                            tint = Color.Black
//                        )
//                    }

//                    if (uiState.localPhotoPath != null || uiState.photoUri != null || uiState.photoUrl.isNotEmpty()) {
//                        IconButton(
//                            onClick = {
//                                uiState.localPhotoPath?.let { File(it).delete() }
//                                userViewModel.uiState = userViewModel.uiState.copy(
//                                    photoUri = null,
//                                    localPhotoPath = null,
//                                    photoUrl = ""
//                                )
//                                clearSavedProfileImage(context)
//                            },
//                            modifier = Modifier
//                                .align(Alignment.TopEnd)
//                                .size(30.dp)
//                                .clip(CircleShape)
//                                .background(Color.White)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = "Remove Photo",
//                                tint = Color.Red
//                            )
//                        }
//                    }
                }

                // ✏️🗑 RIGHT actions
                Row {
                    IconButton(
                        onClick = { onEdit(post) },
                        modifier = Modifier.size(actionSize)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { onDelete(post) },
                        modifier = Modifier.size(actionSize)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = post.description,
            color = Color.White,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        post.imageLocalPath?.let { path ->
            val bitmap = BitmapFactory.decodeFile(path)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

// Comments toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "💬 ${comments.size} Comments",
                color = Color.Gray,
                modifier = Modifier.clickable { showCommentInput = !showCommentInput }
            )
        }

// Input for new comment
        if (showCommentInput) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    label = { Text("Write a comment...") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (newCommentText.isNotBlank()) {
                        comments = comments + Comment(
                            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            username = userViewModel.uiState.username,
                            text = newCommentText
                        )
                        newCommentText = ""
                    }
                }) {
                    Text("Post")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

// Show comments
        Column(modifier = Modifier.fillMaxWidth()) {
            comments.forEachIndexed { index, comment ->
                var isEditing by remember { mutableStateOf(false) }
                var editText by remember { mutableStateOf(comment.text) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End // Align comment to the right
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editText,
                            onValueChange = { editText = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            // Update comment
                            comments = comments.toMutableList().also { it[index] = comment.copy(text = editText) }
                            isEditing = false
                        }) {
                            Text("Save")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(onClick = {
                            isEditing = false
                            editText = comment.text
                        }) {
                            Text("Cancel")
                        }
                    } else {
                        Text("${comment.username}: ", color = Color.Cyan, fontSize = 14.sp)
                        Text(comment.text, color = Color.White, fontSize = 14.sp)

                        Spacer(modifier = Modifier.width(8.dp))

                        // Edit button
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Comment", tint = Color.Yellow)
                        }

                        // Delete button
                        IconButton(onClick = {
                            comments = comments.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Comment", tint = Color.Red)
                        }
                    }
                }
            }
        }



    }
}


