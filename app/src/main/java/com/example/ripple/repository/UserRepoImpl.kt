package com.example.ripple.repository

import com.example.ripple.model.UserModel
import com.example.ripple.viewmodel.ReportModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserRepoImpl : UserRepo {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("User")

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                callback(it.isSuccessful, it.exception?.message ?: "Login Success")
            }
    }

    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Registration Success", auth.currentUser?.uid ?: "")
                } else {
                    callback(false, it.exception?.message ?: "Registration Failed", "")
                }
            }
    }

    override fun addUserToDatabase(userID: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        ref.child(userID).setValue(model)
            .addOnCompleteListener {
                callback(it.isSuccessful, it.exception?.message ?: "User added")
            }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                callback(it.isSuccessful, it.exception?.message ?: "Reset email sent")
            }
    }

    override fun getUserById(userID: String, callback: (Boolean, String, UserModel?) -> Unit) {
        ref.child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(user != null, "Fetched", user)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun getAllUsers(callback: (Boolean, String, List<UserModel>?) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull {
                    it.getValue(UserModel::class.java)
                }
                callback(true, "Users fetched", users)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }


    override fun editProfile(userID: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        ref.child(userID).updateChildren(model.toMap())
            .addOnCompleteListener {
                callback(it.isSuccessful, it.exception?.message ?: "Profile updated")
            }
    }

    override fun deleteAccount(userID: String, callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser ?: run {
            callback(false, "User not logged in")
            return
        }

        ref.child(userID).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                user.delete().addOnCompleteListener { deleteTask ->
                    callback(
                        deleteTask.isSuccessful,
                        deleteTask.exception?.message ?: "Account deleted"
                    )
                }
            } else {
                callback(false, it.exception?.message ?: "Database delete failed")
            }
        }
    }

    override fun updateEmailWithReauth(
        oldEmail: String,
        oldPassword: String,
        newEmail: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser ?: return callback(false, "User not logged in")

        val credential = EmailAuthProvider.getCredential(oldEmail, oldPassword)
        user.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                user.updateEmail(newEmail)
                    .addOnCompleteListener { task ->
                        callback(task.isSuccessful, task.exception?.message ?: "Email updated")
                    }
            } else {
                callback(false, it.exception?.message ?: "Re-auth failed")
            }
        }
    }

    override fun updatePasswordWithReauth(
        oldEmail: String,
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser ?: return callback(false, "User not logged in")

        val credential = EmailAuthProvider.getCredential(oldEmail, oldPassword)
        user.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        callback(task.isSuccessful, task.exception?.message ?: "Password updated")
                    }
            } else {
                callback(false, it.exception?.message ?: "Re-auth failed")
            }
        }
    }

    override fun updateProfileImage(userID: String, imageUrl: String?, callback: (Boolean, String) -> Unit) {
        ref.child(userID).child("photoUrl").setValue(imageUrl)
            .addOnCompleteListener {
                callback(it.isSuccessful, it.exception?.message ?: "Photo updated")
            }
    }

    override fun updatePasswordInDatabase(
        userID: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userID).child("password").setValue(newPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Password updated in database")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun reportProblem(
        userId: String,
        message: String,
        callback: (Boolean, String) -> Unit
    ) {
        val reportRef = database.getReference("Reports").push()
        val data = mapOf(
            "id" to reportRef.key,
            "userId" to userId,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        reportRef.setValue(data).addOnCompleteListener {
            callback(it.isSuccessful, it.exception?.message ?: "Report submitted")
        }
    }

    override fun getAllReports(callback: (Boolean, String, List<ReportModel>) -> Unit) {
        database.getReference("Reports")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reports = snapshot.children.mapNotNull { it.getValue(ReportModel::class.java) }
                    callback(true, "Reports fetched", reports)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun sendFeedback(reportId: String, feedback: String, callback: (Boolean, String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Reports")
        ref.child(reportId).child("feedback").setValue(feedback)
            .addOnCompleteListener {
                callback(it.isSuccessful, it.exception?.message ?: "Feedback sent")
            }
    }



}
