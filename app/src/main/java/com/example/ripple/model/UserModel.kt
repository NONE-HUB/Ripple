package com.example.ripple.model

data class UserModel(
    val userId : String = "",
    val firstName : String = "",
    val middleName : String = "",
    val lastName : String = "",
    val email : String = "",
    val gender : String = "",
    val dob : String = "",
    val password : String = ""
){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "userId" to userId,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "gender" to gender,
            "dob" to dob,
            "password" to password,
        )
    }
}
