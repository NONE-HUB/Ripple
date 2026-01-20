package com.example.ripple.repository

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ripple.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.EmailAuthProvider


class UserRepoImpl : UserRepo {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()

    val ref : DatabaseReference = database.getReference("User")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Login Success")
                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Registration Success","${auth.currentUser?.uid}")
                }else{
                    callback(false,"${it.exception?.message}","")
                }
            }
    }

    override fun addUserToDatabase(
        userID: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {

//        val id  = ref.push().key.toString()

        ref.child(userID).setValue(model)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Registration Success")
                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Password reset email sent")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getUserById(
        userID: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(userID)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null){
                            callback(true,"Profile Fetched",user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false,"${error.message}",null)
                }
            })
    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val allUser = mutableListOf<UserModel>()
                    for (data in snapshot.children) {
                        val user = data.getValue(UserModel::class.java)
                        if (user != null) {
                            allUser.add(user)
                        }
                    }
                    callback(true, "Users fetched successfully", allUser)
                } else {
                    callback(true, "No users found", emptyList())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun editProfile(
        userID: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userID).updateChildren(model.toMap())
            .addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Profile Updated")
                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun deleteAccount(
        userID: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userID).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Account Deleted")
                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun updateEmailWithReauth(
        oldEmail: String,
        oldPassword: String,
        newEmail: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "User not logged in")
            return
        }

        val credential = EmailAuthProvider.getCredential(oldEmail, oldPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updateEmail(newEmail)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                callback(true, "Email updated successfully")
                            } else {
                                callback(false, updateTask.exception?.message ?: "Email update failed")
                            }
                        }
                } else {
                    callback(false, reauthTask.exception?.message ?: "Re-authentication failed")
                }
            }
    }


    override fun updatePasswordWithReauth(
        oldEmail: String,
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "User not logged in")
            return
        }

        val credential = EmailAuthProvider.getCredential(oldEmail, oldPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                callback(true, "Password updated successfully")
                            } else {
                                callback(false, updateTask.exception?.message ?: "Password update failed")
                            }
                        }
                } else {
                    callback(false, reauthTask.exception?.message ?: "Re-authentication failed")
                }
            }
    }



}