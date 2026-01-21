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
