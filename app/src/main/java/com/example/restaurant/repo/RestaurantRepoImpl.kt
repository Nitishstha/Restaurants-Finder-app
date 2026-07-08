package com.example.restaurant.repo
import com.example.restaurant.Model.BookingModel
import com.example.restaurant.Model.RestaurantModel
import com.google.firebase.database.*

class RestaurantRepoImpl : RestaurantRepo {
    private val db get() = FirebaseDatabase.getInstance()
    private val restaurantRef get() = db.getReference("restaurants")
    private val bookingRef get() = db.getReference("bookings")

    override fun addRestaurant(restaurantModel: RestaurantModel, callback: (Boolean, String) -> Unit) {
        try {
            val id = restaurantRef.push().key ?: ""
            restaurantModel.id = id
            restaurantRef.child(id).setValue(restaurantModel).addOnCompleteListener {
                callback(it.isSuccessful, if(it.isSuccessful) "Added" else "Failed")
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    override fun fetchAllRestaurants(callback: (List<RestaurantModel>?, Boolean, String) -> Unit) {
        try {
            restaurantRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(RestaurantModel::class.java) }
                    callback(list, true, "Success")
                }
                override fun onCancelled(error: DatabaseError) { callback(null, false, error.message) }
            })
        } catch (e: Exception) {
            callback(null, false, e.message ?: "Firebase not initialized")
        }
    }

    fun toggleSaveRestaurant(restaurantId: String, userId: String, currentList: List<String>, callback: (Boolean, String) -> Unit) {
        try {
            val newList = currentList.toMutableList()
            if (newList.contains(userId)) newList.remove(userId) else newList.add(userId)

            restaurantRef.child(restaurantId).child("savedBy").setValue(newList)
                .addOnCompleteListener { callback(it.isSuccessful, if(it.isSuccessful) "Success" else "Failed") }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    fun makeBooking(booking: BookingModel, callback: (Boolean, String) -> Unit) {
        try {
            val id = bookingRef.push().key ?: ""
            booking.bookingId = id
            bookingRef.child(id).setValue(booking).addOnCompleteListener { callback(it.isSuccessful, "Booking Sent") }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    override fun fetchAllBookings(callback: (List<BookingModel>?, Boolean, String) -> Unit) {
        try {
            bookingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }
                    callback(list, true, "Success")
                }
                override fun onCancelled(error: DatabaseError) { callback(null, false, error.message) }
            })
        } catch (e: Exception) {
            callback(null, false, e.message ?: "Error")
        }
    }

    override fun updateBookingStatus(bookingId: String, status: String, tableNo: String, callback: (Boolean, String) -> Unit) {
        try {
            val updates = mapOf("status" to status, "tableNo" to tableNo)
            bookingRef.child(bookingId).updateChildren(updates).addOnCompleteListener { callback(it.isSuccessful, "Updated") }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    override fun updateRestaurant(restaurantId: String, data: Map<String, Any?>, callback: (Boolean, String) -> Unit) {
        try {
            restaurantRef.child(restaurantId).updateChildren(data).addOnCompleteListener { callback(it.isSuccessful, "Updated") }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }

    override fun deleteRestaurant(restaurantId: String, callback: (Boolean, String) -> Unit) {
        try {
            restaurantRef.child(restaurantId).removeValue().addOnCompleteListener { callback(it.isSuccessful, "Deleted") }
        } catch (e: Exception) {
            callback(false, e.message ?: "Error")
        }
    }
}
