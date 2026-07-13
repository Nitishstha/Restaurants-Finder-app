package com.example.restaurant.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.restaurant.repo.UserRepoImpl
import com.example.restaurant.viewmodel.UserViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.restaurant.ui.theme.RestaurantTheme

class ForgetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgetBody()
        }
    }
}

@Composable
fun ForgetBody() {
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    ForgetBodyContent(
        onForgetPasswordClick = { email ->
            userViewModel.forgetPassword(email) { _, message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        },
        onBackToLoginClick = {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    )
}

@Composable
fun ForgetBodyContent(
    onForgetPasswordClick: (String) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    placeholder = { Text("abc@gmail.com") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onForgetPasswordClick(email) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Forget Password")
                }
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Log in")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .clickable { onBackToLoginClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgetBodyPreview() {
    RestaurantTheme {
        ForgetBodyContent(
            onForgetPasswordClick = {},
            onBackToLoginClick = {}
        )
    }
}
