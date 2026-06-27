package com.example.restaurant.repo

import com.example.restaurant.Model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepo {
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit)
    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit)
    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit)
    fun getUserById(userId: String, callback: (Boolean, UserModel) -> Unit)
    fun getAllUser(callback: (Boolean, List<UserModel>) -> Unit)
    fun logout()
    fun getCurrentUserData(callback: (UserModel?) -> Unit)
    fun getCurrentUser(): FirebaseUser?
    fun deleteUser(userId: String, callback: (Boolean, String) -> Unit)
    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit)
    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)
}
