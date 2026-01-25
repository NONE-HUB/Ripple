package com.example.ripple

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ripple.viewmodel.SocialViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import com.example.ripple.model.CommentModel
import com.example.ripple.model.PostModel

@Composable
fun HomeScreen(viewModel: SocialViewModel) {
    val showPopup = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Post feed
        LazyColumn {
            items(viewModel.posts.value) { post ->
                PostItem(post = post, viewModel = viewModel)
            }
        }

        // Floating + button
        Icon(
            painter = painterResource(id = R.drawable.eye_open),
            contentDescription = "Add Post",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .clickable { showPopup.value = true },
            tint = Color.Blue
        )

        // Post creation popup
        if (showPopup.value) {
            PostCreationPopup(
                onDismiss = { showPopup.value = false },
                onPost = { description, mediaUri, mediaType ->
                    viewModel.createPost(description, mediaUri, mediaType)
                    showPopup.value = false
                }
            )
        }
    }
}

@Composable
fun PostCreationPopup(
    onDismiss: () -> Unit,
    onPost: (description: String, mediaUri: Uri?, mediaType: String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    var mediaType by remember { mutableStateOf("image") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Media preview
            if (mediaUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(mediaUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { onPost(description, mediaUri, mediaType) }) {
                    Text("Post")
                }
                Button(onClick = onDismiss) {
                    Text("Exit")
                }
            }
        }
    }
}
@Composable
fun PostItem(post: PostModel, viewModel: SocialViewModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        Text(text = post.username, style = MaterialTheme.typography.titleMedium)
        Text(text = post.description, style = MaterialTheme.typography.bodyMedium)

        if (post.mediaUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(post.mediaUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val liked = post.likes[viewModel.userId] == true
            Button(onClick = { viewModel.likePost(post.postId, true) }) {
                Text("Like (${post.likes.count { it.value }})")
            }
            Button(onClick = { viewModel.likePost(post.postId, false) }) {
                Text("Dislike (${post.likes.count { !it.value }})")
            }
        }

        // Comments
        post.comments.forEach { comment ->
            CommentItem(postId = post.postId, comment = comment, viewModel = viewModel)
        }

        // Add new comment
        var commentText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { Text("Add a comment") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            if (commentText.isNotBlank()) {
                viewModel.addComment(post.postId, commentText)
                commentText = ""
            }
        }) {
            Text("Comment")
        }
    }
}

@Composable
fun CommentItem(postId: String, comment: CommentModel, viewModel: SocialViewModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, top = 4.dp)) {

        Text(text = "${comment.username}: ${comment.message}")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val liked = comment.likes[viewModel.userId] == true
            Button(onClick = { viewModel.likeComment(postId, comment.commentId, true) }) {
                Text("Like (${comment.likes.count { it.value }})")
            }
            Button(onClick = { viewModel.likeComment(postId, comment.commentId, false) }) {
                Text("Dislike (${comment.likes.count { !it.value }})")
            }
        }

        // Replies
        comment.replies.forEach { reply ->
            CommentItem(postId = postId, comment = reply, viewModel = viewModel)
        }

        // Reply input
        var replyText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = replyText,
            onValueChange = { replyText = it },
            placeholder = { Text("Reply") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            if (replyText.isNotBlank()) {
                viewModel.replyToComment(postId, comment.commentId, replyText)
                replyText = ""
            }
        }) {
            Text("Reply")
        }
    }
}
