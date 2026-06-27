package com.example.restaurant.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.restaurant.R
import com.example.restaurant.Model.UserModel
import com.example.restaurant.repo.UserRepoImpl
import com.example.restaurant.ui.theme.*
import com.example.restaurant.viewmodel.UserViewModel

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RegisterBody() }
    }
}

@Composable
fun RegisterBody() {
    val context = LocalContext.current
    val activity = context as? Activity

    RegisterContent(
        onRegisterClick = { firstName, lastName, email, password, confirmPassword, terms ->
            // Validations
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@RegisterContent
            }
            if (password != confirmPassword) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@RegisterContent
            }
            if (!terms) {
                Toast.makeText(context, "Agree to terms first", Toast.LENGTH_SHORT).show()
                return@RegisterContent
            }

            // Initialize repository inside onClick to avoid Firebase initialization issues in Preview
            val userViewModel = UserViewModel(UserRepoImpl())

            // Call Registration
            userViewModel.register(email, password) { success, message, userId ->
                if (success) {
                    // Create UserModel with the extra info
                    val model = UserModel(
                        userId = userId,
                        email = email,
                        firstName = firstName,
                        lastName = lastName
                    )
                    userViewModel.addUserToDatabase(userId, model) { _, _ ->
                        Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        activity?.finish()
                    }
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        },
        onSignInClick = {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    )
}

@Composable
fun RegisterContent(
    onRegisterClick: (String, String, String, String, String, Boolean) -> Unit,
    onSignInClick: () -> Unit
) {
    // State variables for new fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // UI states for password visibility
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Create Account", fontSize = 30.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
        Text("Join us to find your next favorite meal.", color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(25.dp))

        // First Name Field
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Last Name Field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Password Field with Show/Hide Toggle
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // Using standard material icons if drawable is missing
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_view),
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_view),
                        contentDescription = null
                    )
                }
            }
        )

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 10.dp)) {
            Checkbox(checked = terms, onCheckedChange = { terms = it }, colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary))
            Text("I agree to the Terms & Conditions")
        }

        Button(
            onClick = {
                onRegisterClick(firstName, lastName, email, password, confirmPassword, terms)
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Register", fontWeight = FontWeight.Bold)
        }

        Text(
            text = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) { append("Sign In") }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp)
                .clickable {
                    onSignInClick()
                },
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterBodyPreview() {
    RestaurantTheme {
        RegisterContent(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onSignInClick = {}
        )
    }
}
