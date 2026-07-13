package com.example.restaurant.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurant.Model.UserModel
import com.example.restaurant.repo.UserRepo
import com.google.firebase.auth.FirebaseUser


class UserViewModel(val repo: UserRepo) : ViewModel() {

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    fun logout() {
        repo.logout()
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }

    private val _userData = MutableLiveData<UserModel?>()
    val userData: MutableLiveData<UserModel?> get() = _userData

    private val _allUsers = MutableLiveData<List<UserModel>>()
    val allUsers: MutableLiveData<List<UserModel>> get() = _allUsers

    fun getCurrentUserData() {
        repo.getCurrentUserData { user ->
            _userData.postValue(user)
        }
    }

    fun getUserById(userId: String) {
        repo.getUserById(userId) { success, user ->
            if (success) {
                _userData.postValue(user)
            }
        }
    }

    fun getAllUser() {
        repo.getAllUser { success, data ->
            if (success) {
                _allUsers.postValue(data)
            }
        }
    }

    fun deleteUser(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteUser(userId, callback)
    }

    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(userId, model, callback)
    }
}
