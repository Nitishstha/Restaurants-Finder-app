package com.example.restaurant.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.restaurant.R
import com.example.restaurant.repo.RestaurantRepoImpl
import com.example.restaurant.ui.theme.RestaurantTheme
import com.example.restaurant.viewmodel.RestaurantViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminDashboardBody()
        }
    }
}

@Composable
fun AdminDashboardBody() {
    val context = LocalContext.current
    val repo = remember { RestaurantRepoImpl() }
    val viewModel = remember { RestaurantViewModel(repo) }

    AdminDashboardContent(
        onLogout = {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            (context as? ComponentActivity)?.finish()
        },
        onAddRestaurant = {
            context.startActivity(Intent(context, AddRestaurantActivity::class.java))
        },
        restaurantListScreen = { AdminRestaurantListScreen(viewModel) },
        bookingScreen = { AdminBookingScreen() }
    )
}

@Composable
fun AdminDashboardContent(
    onLogout: () -> Unit,
    onAddRestaurant: () -> Unit,
    restaurantListScreen: @Composable () -> Unit,
    bookingScreen: @Composable () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        Pair("Restaurants", android.R.drawable.ic_menu_today),
        Pair("Requests", android.R.drawable.ic_popup_reminder),
        Pair("Logout", android.R.drawable.ic_menu_close_clear_cancel)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            if (item.first == "Logout") {
                                onLogout()
                            } else {
                                selectedIndex = index
                            }
                        },
                        icon = { Icon(painterResource(item.second), contentDescription = null) },
                        label = { Text(item.first) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedIndex == 0) {
                FloatingActionButton(
                    onClick = onAddRestaurant,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_input_add),
                        contentDescription = "Add Restaurant",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedIndex) {
                0 -> restaurantListScreen()
                1 -> bookingScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    RestaurantTheme {
        AdminDashboardContent(
            onLogout = {},
            onAddRestaurant = {},
            restaurantListScreen = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Manage Restaurants Screen")
                }
            },
            bookingScreen = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Booking Requests Screen")
                }
            }
        )
    }
}
