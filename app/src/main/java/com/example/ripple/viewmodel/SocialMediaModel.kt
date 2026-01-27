package com.example.ripple.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.ripple.model.CommentModel
import com.example.ripple.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class SocialViewModel : ViewModel() {

    var posts = mutableStateOf(listOf<PostModel>())
        private set

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val username = "UserName" // TODO: fetch from UserModel
    private val dbRef = FirebaseDatabase.getInstance().getReference("Posts")

    init {
        loadPosts()
    }

    fun loadPosts() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.value = snapshot.children.mapNotNull { it.getValue(PostModel::class.java) }
                    .sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun createPost(description: String, mediaUri: Uri?, mediaType: String) {
        val postRef = dbRef.push()
        val postId = postRef.key ?: return

        if (mediaUri != null) {
            val storageRef = FirebaseStorage.getInstance()
                .reference.child("posts/$postId/${mediaUri.lastPathSegment}")
            storageRef.putFile(mediaUri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val post = PostModel(postId, userId, username, description, uri.toString(), mediaType)
                    postRef.setValue(post)
                }
            }
        } else {
            val post = PostModel(postId, userId, username, description)
            postRef.setValue(post)
        }
    }

    fun likePost(postId: String, like: Boolean) {
        val postRef = dbRef.child(postId).child("likes").child(userId)
        postRef.setValue(like)
    }

    fun addComment(postId: String, message: String) {
        val commentRef = dbRef.child(postId).child("comments").push()
        val commentId = commentRef.key ?: return
        val comment = CommentModel(commentId, userId, username, message)
        commentRef.setValue(comment)
    }

    fun likeComment(postId: String, commentId: String, like: Boolean) {
        val commentRef = dbRef.child(postId).child("comments").child(commentId).child("likes").child(userId)
        commentRef.setValue(like)
    }

    fun replyToComment(postId: String, commentId: String, replyMessage: String) {
        val replyRef = dbRef.child(postId).child("comments").child(commentId).child("replies").push()
        val replyId = replyRef.key ?: return
        val reply = CommentModel(replyId, userId, username, replyMessage)
        replyRef.setValue(reply)
    }
}
