package com.example.ripple.model

data class UserModel(
    val userId: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val gender: String = "",
    val dob: String = "",
    val password: String = "",
    val photoUrl: String = "",      // ✅ ADD THIS
    val reportProblem: String = ""   // ✅ NEW FIELD
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "firstName" to firstName,
            "middleName" to middleName,
            "lastName" to lastName,
            "username" to username,
            "email" to email,
            "gender" to gender,
            "dob" to dob,
            "password" to password,
            "photoUrl" to photoUrl,    // ✅ ADD THIS
            "reportProblem" to reportProblem  // ✅ ADD THIS
        )
    }
}

// Post data
data class PostModel(
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val description: String = "",
    val mediaUrl: String = "",   // image or video URL
    val mediaType: String = "",  // "image" or "video"
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Map<String, Boolean> = emptyMap(), // userId -> true (like) / false (dislike)
    val comments: List<CommentModel> = emptyList()
)

data class CommentModel(
    val commentId: String = "",
    val userId: String = "",
    val username: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Map<String, Boolean> = emptyMap(),
    val replies: List<CommentModel> = emptyList()
)

