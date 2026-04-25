package com.example.feeder.feature.auth.presentation


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feeder.R
import com.example.feeder.feature.auth.presentation.components.GoogleSignInButton

@Composable
fun LoginContent (
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onLoginClick: (email: String, password: String) -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Text("Sign in")
    OutlinedTextField(
        value = email,
        onValueChange = {email = it},
        modifier = Modifier.fillMaxWidth(),
        label = {Text("Email")}
    )

    OutlinedTextField(
        value = password,
        onValueChange = {password = it},
        modifier = Modifier.fillMaxWidth(),
        label = {Text("Password")}
    )

    TextButton(onClick = onForgotPasswordClick) {
        Text("ForgotPassword")
    }

    Button (
        onClick = {onLoginClick(email,password)},
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Sign in")
    }

    GoogleSignInButton(
        onClick = onGoogleSignInClick
    )
    TextButton(onClick = onGoToRegister) {
        Text("Register")
    }
}