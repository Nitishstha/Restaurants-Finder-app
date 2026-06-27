package com.example.restaurant.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurant.R
import com.example.restaurant.repo.UserRepoImpl
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import com.example.restaurant.view.UserDashboardActivity

@Composable
fun LoginBody() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                "Sign In",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Text(
                "Discover the best restaurants around you with our personalized finder.",
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { visibility = !visibility }) {
                        Icon(
                            painter = painterResource(
                                if (visibility) android.R.drawable.ic_menu_view else android.R.drawable.ic_menu_view
                            ),
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Please fill all fields") }
                        return@Button
                    }

                    // Initialize repository inside onClick to avoid Firebase initialization issues in Preview
                    val repo = UserRepoImpl()

                    // --- ROLE BASED NAVIGATION LOGIC ---
                    if (email == "admin@gmail.com" && password == "admin123") {
                        Toast.makeText(context, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, DashboardActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                    } else {
                        // Standard User Login via Repository
                        repo.login(email, password) { success, message ->
                            if (success) {
                                val intent = Intent(context, UserDashboardActivity::class.java)
                                context.startActivity(intent)
                                activity?.finish()
                            } else {
                                coroutineScope.launch { snackbarHostState.showSnackbar(message ?: "Login failed") }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 25.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = buildAnnotatedString {
                    append("New to Restaurant Finder? ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append("Sign Up")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(context, RegistrationActivity::class.java))
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginBody()
}
