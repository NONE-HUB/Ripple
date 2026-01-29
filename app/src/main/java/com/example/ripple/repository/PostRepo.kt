// PostRepo.kt
package com.example.ripple.repository

import com.example.ripple.model.Posted


interface PostRepo {
    fun addPost(
        post: Posted,
        callback: (Boolean, String) -> Unit
    )

    fun getAllPosts(
        callback: (List<Posted>) -> Unit
    )



}