package com.example.restaurant.repo

import com.example.restaurant.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserRepoImpl : UserRepo {

    private val auth: FirebaseAuth get() = try { FirebaseAuth.getInstance() } catch (e: Exception) { throw e }
    private val database: FirebaseDatabase get() = try { FirebaseDatabase.getInstance() } catch (e: Exception) { throw e }
    private val ref: DatabaseReference get() = database.getReference("Users")

    override fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        try {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) callback(true, "Login Success")
                else callback(false, it.exception?.message)
            }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }

    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Register Success", auth.currentUser?.uid ?: "")
                } else {
                    callback(false, it.exception?.message ?: "Error", "")
                }
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error", "")
        }
    }

    override fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        try {
            ref.child(userId).setValue(model).addOnCompleteListener {
                if (it.isSuccessful) callback(true, "User data saved")
                else callback(false, it.exception?.message ?: "Error")
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    override fun getUserById(userId: String, callback: (Boolean, UserModel) -> Unit) {
        try {
            ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) callback(true, user)
                    else callback(false, UserModel())
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false, UserModel())
                }
            })
        } catch (e: Exception) {
            callback(false, UserModel())
        }
    }

    override fun getAllUser(callback: (Boolean, List<UserModel>) -> Unit) {
        try {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<UserModel>()
                    for (data in snapshot.children) {
                        data.getValue(UserModel::class.java)?.let { list.add(it) }
                    }
                    callback(true, list)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false, emptyList())
                }
            })
        } catch (e: Exception) {
            callback(false, emptyList())
        }
    }

    override fun logout() {
        try { auth.signOut() } catch (e: Exception) {}
    }

    override fun getCurrentUserData(callback: (UserModel?) -> Unit) {
        try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                getUserById(uid) { success, model ->
                    if (success) callback(model) else callback(null)
                }
            } else {
                callback(null)
            }
        } catch (e: Exception) {
            callback(null)
        }
    }

    override fun getCurrentUser(): FirebaseUser? = try { auth.currentUser } catch (e: Exception) { null }

    override fun deleteUser(userId: String, callback: (Boolean, String) -> Unit) {
        try {
            ref.child(userId).removeValue().addOnCompleteListener {
                if (it.isSuccessful) callback(true, "Deleted")
                else callback(false, it.exception?.message ?: "Error")
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    override fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        try {
            val updates = model.toMap()
            ref.child(userId).updateChildren(updates).addOnCompleteListener {
                if (it.isSuccessful) callback(true, "Profile Updated")
                else callback(false, it.exception?.message ?: "Error")
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        try {
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) callback(true, "Reset link sent")
                else callback(false, it.exception?.message ?: "Error")
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }
    fun toggleSaveRestaurant(restaurantId: String, callback: (Boolean) -> Unit) {
        try {
            val uid = auth.currentUser?.uid ?: return
            val savedRef = database.getReference("Saved").child(uid).child(restaurantId)

            savedRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    savedRef.removeValue().addOnCompleteListener { callback(true) } // Unsave
                } else {
                    savedRef.setValue(true).addOnCompleteListener { callback(true) } // Save
                }
            }
        } catch (e: Exception) {
            callback(false)
        }
    }
}
