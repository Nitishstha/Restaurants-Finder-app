package com.example.restaurant.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.restaurant.Model.RestaurantModel
import com.example.restaurant.viewmodel.RestaurantViewModel

@Composable
fun SearchScreen(viewModel: RestaurantViewModel) {
    var query by remember { mutableStateOf("") }
    val restaurants by viewModel.restaurants
    val context = LocalContext.current

    SearchContent(
        query = query,
        onQueryChange = { query = it },
        restaurants = restaurants,
        onRestaurantClick = { restaurant ->
            Toast.makeText(context, "Opening booking for ${restaurant.name}", Toast.LENGTH_SHORT).show()
        },
        onSaveClick = { restaurantId ->
            Toast.makeText(context, "Saved ${restaurantId}", Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun SearchContent(
    query: String,
    onQueryChange: (String) -> Unit,
    restaurants: List<RestaurantModel>,
    onRestaurantClick: (RestaurantModel) -> Unit,
    onSaveClick: (String) -> Unit
) {
    val filtered = restaurants.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.cuisine.contains(query, ignoreCase = true)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search by name or cuisine...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { restaurant ->
                UserRestaurantCard(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant) },
                    onSaveClick = onSaveClick
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchContent(
        query = "",
        onQueryChange = {},
        restaurants = listOf(
            RestaurantModel(id = "1", name = "Pizza Hut", cuisine = "Italian", location = "Downtown")
        ),
        onRestaurantClick = {},
        onSaveClick = {}
    )
}
