package com.example.ripple.repository

import com.example.ripple.model.Posted
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PostRepoImpl : PostRepo {

    override fun addPost(
        post: Posted,
        callback: (Boolean, String) -> Unit
    ) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("posts")
            .push()

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
        FirebaseDatabase.getInstance()
            .getReference("posts")
            .addValueEventListener(object : ValueEventListener {

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
}
