package com.example.ripple.repository

import com.example.ripple.model.Posted
import com.google.firebase.database.*

class PostRepoImpl : PostRepo {

    private val dbRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("posts")

    override fun addPost(
        post: Posted,
        callback: (Boolean, String) -> Unit
    ) {
        val ref = dbRef.push()
        val newPost = post.copy(postId = ref.key ?: "")

        ref.setValue(newPost)
            .addOnSuccessListener {
                callback(true, "Post added successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Error adding post")
            }
    }

    override fun getAllPosts(
        callback: (List<Posted>) -> Unit
    ) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Posted>()
                for (child in snapshot.children) {
                    val post = child.getValue(Posted::class.java)
                    if (post != null) list.add(post)
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    // ✅ DELETE POST (Realtime DB)
    fun deletePost(
        postId: String,
        callback: (Boolean, String) -> Unit
    ) {
        dbRef.child(postId)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "Post deleted")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Delete failed")
            }
    }

    // ✅ UPDATE POST (Realtime DB)
    fun updatePost(
        post: Posted,
        callback: (Boolean, String) -> Unit
    ) {
        dbRef.child(post.postId)
            .setValue(post)
            .addOnSuccessListener {
                callback(true, "Post updated")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Update failed")
            }
    }




}