package com.example.restaurant.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.restaurant.Model.BookingModel
import com.example.restaurant.repo.RestaurantRepoImpl
import com.example.restaurant.ui.theme.RestaurantTheme
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

class AdminBookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AdminBookingScreen() }
    }
}

@Composable
fun AdminBookingScreen() {
    val repo = remember { RestaurantRepoImpl() }
    val context = LocalContext.current
    var bookings by remember { mutableStateOf<List<BookingModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        repo.fetchAllBookings { list, success, _ ->
            if (success && list != null) {
                bookings = list
            }
        }
    }

    AdminBookingContent(
        bookings = bookings,
        onBack = { (context as? ComponentActivity)?.finish() },
        onAction = { booking, status, tableNo ->
            repo.updateBookingStatus(booking.bookingId, status, tableNo) { success, _ ->
                if (success) {
                    repo.fetchAllBookings { list, _, _ -> if (list != null) bookings = list }
                    Toast.makeText(context, "Status updated to $status", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

@Composable
fun AdminBookingContent(
    bookings: List<BookingModel>,
    onBack: () -> Unit,
    onAction: (BookingModel, String, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Approved", "Declined")

    val filteredList = when (selectedTab) {
        0 -> bookings.filter { it.status == "Pending" }
        1 -> bookings.filter { it.status == "Approved" }
        else -> bookings.filter { it.status == "Declined" }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Surface(color = MaterialTheme.colorScheme.primary, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 4.dp, vertical = 8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Text("Manage Requests", color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No ${tabs[selectedTab]} requests found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { booking ->
                    BookingApprovalCard(booking = booking) { status, tableNo ->
                        onAction(booking, status, tableNo)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminBookingPreview() {
    val mockBookings = listOf(
        BookingModel(bookingId = "1", restaurantName = "Burger Joint", userEmail = "user1@example.com", date = "2023-10-01", time = "12:00", status = "Pending"),
        BookingModel(bookingId = "2", restaurantName = "Pasta House", userEmail = "user2@example.com", date = "2023-10-02", time = "13:00", status = "Approved", tableNo = "5")
    )
    RestaurantTheme {
        AdminBookingContent(
            bookings = mockBookings,
            onBack = {},
            onAction = { _, _, _ -> }
        )
    }
}

@Composable
fun BookingApprovalCard(booking: BookingModel, onAction: (String, String) -> Unit) {
    var tableInput by remember { mutableStateOf(booking.tableNo) }
    val isPending = booking.status == "Pending"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${booking.restaurantName}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("From: ${booking.userEmail}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Text("${booking.date} | ${booking.time}", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (isPending) {
                OutlinedTextField(
                    value = tableInput,
                    onValueChange = { tableInput = it },
                    label = { Text("Assign Table (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onAction("Approved", tableInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Approve") }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onAction("Declined", "") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Decline") }
                }
            } else {
                Surface(
                    color = if (booking.status == "Approved") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "STATUS: ${booking.status} ${if (booking.tableNo.isNotEmpty()) " (Table ${booking.tableNo})" else ""}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (booking.status == "Approved") Color(0xFF2E7D32) else Color.Red
                    )
                }
            }
        }
    }
}
