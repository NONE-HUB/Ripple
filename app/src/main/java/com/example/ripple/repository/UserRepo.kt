package com.example.ripple.repository

import com.example.ripple.model.UserModel

interface UserRepo {

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun getAllUsers(
        callback: (Boolean, String, List<UserModel>?) -> Unit
    )

    fun editProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateEmailWithReauth(
        oldEmail: String,
        oldPassword: String,
        newEmail: String,
        callback: (Boolean, String) -> Unit
    )

    fun updatePasswordWithReauth(
        oldEmail: String,
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateProfileImage(
        userId: String,
        imageUrl: String?,
        callback: (Boolean, String) -> Unit
    )

    fun updatePasswordInDatabase(
        userID: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    )
}
