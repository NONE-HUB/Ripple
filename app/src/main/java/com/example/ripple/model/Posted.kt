package com.example.ripple.model

data class Posted(
    val postId: String = "",
    val userId: String = "",
    val description: String = "",

    // ✅ LOCAL IMAGE PATH (saved on device)
    val imageLocalPath: String? = null,

    // ✅ USER INFO (for feed display)
    val userName: String = "",
    val userPhotoUrl: String = "",

    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0,
    val downvotes: Int = 0
)
