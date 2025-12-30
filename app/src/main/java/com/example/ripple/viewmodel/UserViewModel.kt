package com.example.ai37b.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ripple.model.UserModel
import com.example.ripple.repository.UserRepo

class UserViewModel(val repo : UserRepo) : ViewModel() {

    fun login(email: String, password: String,
              callback:(Boolean, String) -> Unit) {
        repo.login(email ,password, callback )
    }

    fun register(email: String,password: String,
                 callback: (Boolean, String, String) -> Unit){
        repo.register(email , password , callback  )
    }

    fun addUserToDatabase(
        userId: String, model: UserModel, callback: (Boolean, String) -> Unit
    ) {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun forgetPassword(email: String,
                        callback: (Boolean, String) -> Unit){
        repo.forgetPassword(email, callback )
    }

    private val _users = MutableLiveData<UserModel?>()
    val users : MutableLiveData<UserModel?>
        get() = _users

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers : MutableLiveData<List<UserModel>?>
        get() = _allUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading : MutableLiveData<Boolean>
        get() = _loading

    fun getUserById(userID: String,){
        repo.getUserById(userID){
            success,message,data->
            if (success){
                _users.postValue(data)
            }
        }
    }

    fun getAllUser(){
        repo.getAllUser {
            success,message,data->
            if (success){
                _allUsers.postValue(data)
                _loading.postValue(false)
            }
            _loading.postValue(false)
        }
    }

    fun editProfile(userID: String,model: UserModel,
                    callback: (Boolean, String) -> Unit){
        repo.editProfile(userID , model , callback )
    }

    fun deleteAccount(userID: String,
                      callback: (Boolean, String) -> Unit){
        repo.deleteAccount(userID , callback )
    }
}