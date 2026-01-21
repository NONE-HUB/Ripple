package com.example.ripple.repository

import com.example.ripple.model.UserModel
import com.example.ripple.viewmodel.ReportModel
import com.example.ripple.viewmodel.UserViewModel
import com.google.firebase.database.FirebaseDatabase

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

    fun reportProblem(
        userId: String,
        message: String,
        callback: (Boolean, String) -> Unit
    )

    fun getAllReports(
        callback: (Boolean, String, List<ReportModel>) -> Unit
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

    fun sendFeedback(reportId: String, feedback: String, callback: (Boolean, String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Reports")
        ref.child(reportId).child("feedback").setValue(feedback)
            .addOnCompleteListener {
                callback(it.isSuccessful, it.exception?.message ?: "Feedback sent")
            }
    }

}
