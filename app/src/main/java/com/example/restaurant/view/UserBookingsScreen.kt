package com.example.restaurant.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurant.Model.BookingModel
import com.example.restaurant.repo.RestaurantRepoImpl
import com.example.restaurant.repo.UserRepoImpl
import com.example.restaurant.ui.theme.PrimaryOrange

@Composable
fun UserBookingsScreen() {
    val repo = remember { RestaurantRepoImpl() }
    val userRepo = remember { UserRepoImpl() }
    val currentUserEmail = userRepo.getCurrentUser()?.email

    var myBookings by remember { mutableStateOf<List<BookingModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repo.fetchAllBookings { list, success, _ ->
            if (success && list != null) {
                myBookings = list.filter { it.userEmail == currentUserEmail }
            }
            isLoading = false
        }
    }

    UserBookingsContent(bookings = myBookings, isLoading = isLoading)
}

@Composable
fun UserBookingsContent(bookings: List<BookingModel>, isLoading: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("My Reservations", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isLoading) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            }
        } else if (bookings.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No bookings found.", color = Color.Gray)
                }
            }
        } else {
            items(bookings) { booking ->
                BookingCard(booking = booking)
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(booking.restaurantName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Date: ${booking.date} | Time: ${booking.time}", color = Color.Gray)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Status: ${booking.status}",
                    color = if(booking.status == "Approved") Color(0xFF4CAF50) else Color.Black,
                    fontWeight = FontWeight.Medium
                )
                if(!booking.tableNo.isNullOrEmpty()) {
                    Text("Table: ${booking.tableNo}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserBookingsPreview() {
    UserBookingsContent(
        bookings = listOf(
            BookingModel(restaurantName = "Sample Restaurant", date = "12 Dec", time = "8:00 PM", status = "Pending")
        ),
        isLoading = false
    )
}
