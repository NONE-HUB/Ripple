package com.example.ripple.viewmodel

import UserModel
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ripple.clearSavedProfileImage
import com.example.ripple.repository.UserRepo
import com.example.ripple.repository.UserRepoImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.firebase.storage.FirebaseStorage
import java.io.File


// UI state for Compose
data class UserUiState(
    val photoUrl: String = "", // URL from Firebase
    val photoUri: Uri? = null, // Local Uri for preview
    val localPhotoPath: String? = null,   // ✅ ADD THIS
    val isLoading: Boolean = true,
    val error: String? = null,
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val dob: String? = null,
    val gender: String? = null,
    val imageVersion: Int = 0 ,// <-- add this
)




class UserViewModel(private val repo: UserRepo = UserRepoImpl()) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    // Compose state
    var uiState by mutableStateOf(UserUiState())




    init {
        loadUser()
    }





    /** Load current user info from repo */
//    private fun loadUser() {
//        if (userId.isEmpty()) {
//            uiState = uiState.copy(isLoading = false, error = "User not logged in")
//            return
//        }
//        viewModelScope.launch {
//            repo.getUserById(userId) { success, message, user ->
//                if (success && user != null) {
//                    uiState = uiState.copy(
//                        photoUrl = user.photoUrl ?: "",
//                        firstName = user.firstName,
//                        middleName = user.middleName,
//                        lastName = user.lastName,
//                        username = user.username,
//                        email = user.email,
//                        dob = user.dob,
//                        gender = user.gender,
//                        isLoading = false
//                    )
//                } else {
//                    uiState = uiState.copy(
//                        isLoading = false,
//                        error = message.ifEmpty { "Failed to load user" }
//                    )
//                }
//            }
//        }
//    }

    fun loadUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repo.getUserById(userId) { success, _, user ->
            if (success && user != null) {
                uiState = uiState.copy(
                    photoUrl = user.photoUrl ?: "",
                    firstName = user.firstName,
                    middleName = user.middleName,
                    lastName = user.lastName,
                    username = user.username,
                    email = user.email,
                    dob = user.dob,
                    gender = user.gender,
                    isLoading = false
                )
            }
        }
    }

    /** Delete current user account */
    fun deleteAccount(onComplete: (Boolean, String) -> Unit) {
        if (userId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        viewModelScope.launch {
            // Make sure your UserRepo has a method deleteAccount(userId, callback)
            repo.deleteAccount(userId) { success: Boolean, message: String ->
                onComplete(success, message)
            }
        }
    }

    /** Update user photo in UI state (should also update Firestore / Storage) */
    /** Update user photo in UI state (should also update Firestore / Storage) */
//    fun updatePhoto(newUrl: String) {
//        uiState = uiState.copy(photoUrl = newUrl)
//    }

//    fun updatePhoto(uri: Uri?) {
//        if (userId.isEmpty()) {
//            uiState = uiState.copy(error = "User not logged in")
//            return
//        }
//
//        // Show local preview immediately
//        uiState = uiState.copy(photoUri = uri, isLoading = true)
//
//        if (uri == null) {
//            // Removing photo
//            uiState = uiState.copy(photoUrl = "", photoUri = null, isLoading = false)
//            repo.updateProfileImage(userId, "") { success, _ -> }
//            return
//        }
//
//        // Upload to Firebase Storage
//        val storageRef = FirebaseStorage.getInstance()
//            .reference.child("users/$userId/profile.jpg")
//
//        storageRef.putFile(uri)
//            .addOnSuccessListener {
//                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    val url = downloadUri.toString()
//                    repo.updateProfileImage(userId, url) { success, message ->
//                        if (success) {
//                            uiState = uiState.copy(photoUrl = url, isLoading = false)
//                        } else {
//                            uiState = uiState.copy(isLoading = false, error = "Failed to save photo URL: $message")
//                        }
//                    }
//                }.addOnFailureListener { e ->
//                    uiState = uiState.copy(isLoading = false, error = "Failed to get download URL: ${e.message}")
//                }
//            }
//            .addOnFailureListener { e ->
//                uiState = uiState.copy(isLoading = false, error = "Failed to upload image: ${e.message}")
//            }
//    }

//    fun updatePhoto(uri: Uri?) {
//        if (uri == null) {
//            // Remove photo
//            repo.updateProfileImage(userId, "") { success, _ ->
//                if (success) {
//                    uiState = uiState.copy(photoUrl = "")
//                }
//            }
//            return
//        }
//
//        // Show local preview immediately
//        uiState = uiState.copy(photoUrl = "", photoUri = uri, isLoading = true)
//
//        val storageRef = FirebaseStorage.getInstance()
//            .reference.child("users/$userId/profile.jpg")
//
//        storageRef.putFile(uri)
//            .addOnSuccessListener {
//                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    val url = downloadUri.toString()
//                    repo.updateProfileImage(userId, url) { success, _ ->
//                        if (success) {
//                            uiState = uiState.copy(photoUrl = url, photoUri = null, isLoading = false)
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener {
//                uiState = uiState.copy(isLoading = false)
//            }
//    }

    fun setLocalPhoto(path: String) {
        val uri = Uri.fromFile(File(path))
        uiState = uiState.copy(
            localPhotoPath = path,
            photoUri = uri,
            photoUrl = "",
            imageVersion = uiState.imageVersion + 1
        )
    }

    fun clearPhoto(context: Context) {
        uiState.localPhotoPath?.let { File(it).delete() }
        uiState = uiState.copy(
            localPhotoPath = null,
            photoUri = null,
            photoUrl = "",
            imageVersion = uiState.imageVersion + 1
        )
        clearSavedProfileImage(context)
    }


    fun updateDob(newDob: String) {
        uiState = uiState.copy(dob = newDob)
    }



    fun updateNameAndDob(first: String, middle: String, last: String, dob: String) {
        // Update local UI state immediately
        uiState = uiState.copy(
            firstName = first,
            middleName = middle,
            lastName = last,
            dob = dob
        )

        // Persist to Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updatedFields = mapOf(
            "firstName" to first,
            "middleName" to middle,
            "lastName" to last,
            "dob" to dob
        )

        repo.updateUserFields(userId, updatedFields) { success, message ->
            if (!success) {
                uiState = uiState.copy(error = "Failed to update profile: $message")
            }
        }
    }





    fun updatePhoto(uri: Uri?) {
        if (userId.isEmpty()) {
            uiState = uiState.copy(error = "User not logged in")
            return
        }

        // Show local preview immediately
        uiState = uiState.copy(photoUri = uri, isLoading = true)

        if (uri == null) {
            // Clear UI immediately
            uiState = uiState.copy(photoUri = null, photoUrl = "", isLoading = true)

            // Delete from Firebase in background
            val storageRef = FirebaseStorage.getInstance().reference.child("users/$userId/profile.jpg")
            storageRef.delete().addOnCompleteListener {
                repo.updateProfileImage(userId, "") { success, _ ->
                    // Optional: just reset loading
                    uiState = uiState.copy(isLoading = false)
                }
            }.addOnFailureListener {
                uiState = uiState.copy(isLoading = false)
            }
            return
        }


        // --- Upload new photo as before ---
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("users/$userId/profile.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()
                    repo.updateProfileImage(userId, url) { success, message ->
                        if (success) {
                            uiState = uiState.copy(photoUrl = url, isLoading = false)
                        } else {
                            uiState = uiState.copy(isLoading = false, error = "Failed to save photo URL: $message")
                        }
                    }
                }.addOnFailureListener { e ->
                    uiState = uiState.copy(isLoading = false, error = "Failed to get download URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                uiState = uiState.copy(isLoading = false, error = "Failed to upload image: ${e.message}")
            }
    }







    /** Authentication / Account functions */
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    fun updateUserField(userId: String, fields: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.updateUserFields(userId, fields, callback)
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun editProfile(userID: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.editProfile(userID, model, callback)
    }

    /** Get a single user by ID */
    fun getUserById(userID: String, callback: (Boolean, String, UserModel?) -> Unit) {
        repo.getUserById(userID) { success: Boolean, message: String, data: UserModel? ->
            callback(success, message, data)
        }
    }

    /** Get all users */
    fun getAllUsers(callback: (Boolean, String, List<UserModel>?) -> Unit) {
        repo.getAllUsers { success: Boolean, message: String, data: List<UserModel>? ->
            callback(success, message, data)
        }
    }


    /** Save photo URL in database */
    private fun savePhotoUrl(url: String) {
        repo.updateProfileImage(userId, url) { success, message ->
            if (success) {
                uiState = uiState.copy(photoUrl = url)
            } else {
                uiState = uiState.copy(error = "Failed to save photo URL: $message")
            }
        }
    }

    fun updatePhotoAndSave(newUrl: String) {
        // Update UI immediately
        uiState = uiState.copy(photoUrl = newUrl)

        // Save URL in Firebase database
        savePhotoUrl(newUrl)
    }




}