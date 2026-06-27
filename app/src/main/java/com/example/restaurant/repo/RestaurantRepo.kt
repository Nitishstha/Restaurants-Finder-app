package com.example.restaurant.repo

import com.example.restaurant.Model.RestaurantModel

interface RestaurantRepo {
    fun addRestaurant(restaurantModel: RestaurantModel, callback: (Boolean, String) -> Unit)
    fun fetchAllRestaurants(callback: (List<RestaurantModel>?, Boolean, String) -> Unit)
    fun updateRestaurant(restaurantId: String, data: Map<String, Any?>, callback: (Boolean, String) -> Unit)
    fun deleteRestaurant(restaurantId: String, callback: (Boolean, String) -> Unit)
    fun fetchAllBookings(callback: (List<com.example.restaurant.Model.BookingModel>?, Boolean, String) -> Unit)
    fun updateBookingStatus(bookingId: String, status: String, tableNo: String, callback: (Boolean, String) -> Unit)
}
