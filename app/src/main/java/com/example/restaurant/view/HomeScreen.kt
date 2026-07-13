package com.example.restaurant.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurant.Model.BookingModel
import com.example.restaurant.Model.RestaurantModel
import com.example.restaurant.repo.RestaurantRepoImpl
import com.example.restaurant.repo.UserRepoImpl
import com.example.restaurant.ui.theme.Blue
import com.example.restaurant.ui.theme.RestaurantTheme
import com.example.restaurant.ui.theme.White
import com.example.restaurant.viewmodel.RestaurantViewModel

@Composable
fun HomeScreen(viewModel: RestaurantViewModel) {
    val restaurants by viewModel.restaurants
    val context = LocalContext.current
    val restaurantRepo = remember { RestaurantRepoImpl() }
    val userRepo = remember { UserRepoImpl() }

    HomeContent(
        restaurants = restaurants,
        onSaveClick = { restaurantId, currentList ->
            val currentUserId = userRepo.getCurrentUser()?.uid ?: ""
            if (currentUserId.isNotEmpty()) {
                // Assuming toggleSaveRestaurant exists in RestaurantRepoImpl
                // Based on previous errors, maybe it's in UserRepoImp?
                // Let's check UserRepoImp again.
                userRepo.toggleSaveRestaurant(restaurantId) { success ->
                    if (!success) {
                        Toast.makeText(context, "Error toggling save", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please login to save places", Toast.LENGTH_SHORT).show()
            }
        },
        onBook = { restaurant, date, time ->
            val currentUserEmail = userRepo.getCurrentUser()?.email ?: ""
            val booking = BookingModel(
                restaurantId = restaurant.id,
                restaurantName = restaurant.name,
                userEmail = currentUserEmail,
                date = date,
                time = time,
                status = "Pending"
            )
            restaurantRepo.makeBooking(booking) { success, msg ->
                if (success) {
                    Toast.makeText(context, "Booking Request Sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

@Composable
fun HomeContent(
    restaurants: List<RestaurantModel>,
    onSaveClick: (String, List<String>) -> Unit,
    onBook: (RestaurantModel, String, String) -> Unit
) {
    var selectedRestaurant by remember { mutableStateOf<RestaurantModel?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White) // Replaced SoftBackground with White as it might be missing
            .padding(16.dp)
    ) {
        Text(
            text = "Find Your Table",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Blue
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(restaurants) { restaurant ->
                UserRestaurantCard(
                    restaurant = restaurant,
                    onClick = {
                        selectedRestaurant = restaurant
                        showDialog = true
                    },
                    onSaveClick = { restaurantId ->
                        onSaveClick(restaurantId, restaurant.savedBy)
                    }
                )
            }
        }
    }

    if (showDialog && selectedRestaurant != null) {
        BookingDialog(
            restaurant = selectedRestaurant!!,
            onDismiss = { showDialog = false },
            onBook = { dateInput, timeInput ->
                onBook(selectedRestaurant!!, dateInput, timeInput)
                showDialog = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RestaurantTheme {
        HomeContent(
            restaurants = listOf(
                RestaurantModel(id = "1", name = "Sample Restaurant", location = "123 Main St", cuisine = "Italian")
            ),
            onSaveClick = { _, _ -> },
            onBook = { _, _, _ -> }
        )
    }
}
