package com.example.restaurant.view

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurant.R
import com.example.restaurant.Model.UserModel
import com.example.restaurant.repo.UserRepoImpl
import com.example.restaurant.ui.theme.PrimaryOrange
import com.example.restaurant.ui.theme.SoftBackground
import com.example.restaurant.ui.theme.White
import com.example.restaurant.viewmodel.UserViewModel
import androidx.compose.ui.graphics.Color.Companion.Black
import com.example.restaurant.ui.theme.Blue

@Composable
fun MoreScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepoImpl() }
    val viewModel = remember { UserViewModel(repo) }
    val userData by viewModel.userData.observeAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val currentUser = viewModel.getCurrentUser()
        if (currentUser?.email == "admin@gmail.com") {
            firstName = "Admin"; lastName = "User"; email = "admin@gmail.com"; isLoading = false
        } else {
            viewModel.getCurrentUserData()
        }
    }

    LaunchedEffect(userData) {
        userData?.let {
            firstName = it.firstName; lastName = it.lastName; email = it.email
            dob = it.dob; gender = it.gender; isLoading = false
        }
    }

    MoreContent(
        firstName = firstName,
        onFirstNameChange = { firstName = it },
        lastName = lastName,
        onLastNameChange = { lastName = it },
        email = email,
        isLoading = isLoading,
        onSaveClick = {
            val uid = viewModel.getCurrentUser()?.uid ?: ""
            viewModel.updateProfile(uid, UserModel(uid, email, firstName, lastName, dob, gender)) { success, msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        },
        onSavedRestaurantsClick = {
            context.startActivity(Intent(context, SavedRestaurantsActivity::class.java))
        },
        onLogoutClick = {
            viewModel.logout()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            (context as ComponentActivity).finish()
        }
    )
}

@Composable
fun MoreContent(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    isLoading: Boolean,
    onSaveClick: () -> Unit,
    onSavedRestaurantsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(SoftBackground).padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, null, modifier = Modifier.size(100.dp), tint = Black)
        Text("Profile Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(30.dp))

        if (isLoading) {
            CircularProgressIndicator(color = PrimaryOrange)
        } else {
            OutlinedTextField(value = firstName, onValueChange = onFirstNameChange, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = lastName, onValueChange = onLastNameChange, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = email, onValueChange = { }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), enabled = false)

            Spacer(modifier = Modifier.height(20.dp))

            if (email != "admin@gmail.com") {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue)
                ) { Text("Save Changes", color = White) }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onSavedRestaurantsClick,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue)
        ) {
            Icon(painterResource(R.drawable.baseline_notifications_24), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Saved Restaurants")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) { Text("Logout", color = White) }
    }
}

@Preview(showBackground = true)
@Composable
fun MoreScreenPreview() {
    MoreContent(
        firstName = "John",
        onFirstNameChange = {},
        lastName = "Doe",
        onLastNameChange = {},
        email = "john@example.com",
        isLoading = false,
        onSaveClick = {},
        onSavedRestaurantsClick = {},
        onLogoutClick = {}
    )
}