package com.example.ripple.repository

import com.example.ripple.model.UserModel

interface UserRepo {

//    {
//        "success":true,
//    "statusCode: 200
//        "message":"login successful"
//    }

    fun login(email: String, password: String,
              callback:(Boolean, String) -> Unit)

    fun register(email: String,password: String,
                 callback: (Boolean, String, String) -> Unit)

    fun addUserToDatabase(userID: String, model: UserModel,
                          callback: (Boolean, String) -> Unit)

    fun forgetPassword(email: String,
                       callback: (Boolean, String) -> Unit)

    fun getUserById(userID: String,
                    callback: (Boolean, String, UserModel?) -> Unit)

    fun getAllUser(callback: (Boolean, String, List<UserModel>) -> Unit)

    fun editProfile(userID: String,model: UserModel,
                    callback: (Boolean, String) -> Unit)

    fun deleteAccount(userID: String,
                      callback: (Boolean, String) -> Unit)

}