package com.example.restaurant.Model


data class BookingModel(
    var bookingId: String = "",
    var restaurantId: String = "",
    var restaurantName: String = "",
    var userEmail: String = "",
    var date: String = "",
    var time: String = "",
    var status: String = "Pending", // Pending, Approved, Declined
    var tableNo: String = ""
)
