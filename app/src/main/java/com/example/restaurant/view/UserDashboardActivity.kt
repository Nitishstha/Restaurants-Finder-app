package com.example.restaurant.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.restaurant.R
import com.example.restaurant.repo.RestaurantRepoImpl
import com.example.restaurant.ui.theme.PrimaryOrange
import com.example.restaurant.ui.theme.White
import com.example.restaurant.viewmodel.RestaurantViewModel

class UserDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserDashboardBody()
        }
    }
}

@Composable
fun UserDashboardBody() {
    val repo = remember { RestaurantRepoImpl() }
    val viewModel = remember { RestaurantViewModel(repo) }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val navItems = listOf(
        Pair("Home", R.drawable.outline_home_24),
        Pair("Search", R.drawable.outline_attach_file_24), // Fallback if search is missing
        Pair("Bookings", R.drawable.baseline_notifications_24),
        Pair("Profile", R.drawable.baseline_add_24) // Fallback if settings is missing
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = White) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(painterResource(item.second), contentDescription = null) },
                        label = { Text(item.first) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryOrange,
                            selectedTextColor = PrimaryOrange,
                            indicatorColor = PrimaryOrange.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedIndex) {
                0 -> HomeScreen(viewModel)
                1 -> SearchScreen(viewModel)
                2 -> UserBookingsScreen()
                3 -> MoreScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserDashboardBodyPreview() {
    UserDashboardBody()
}
