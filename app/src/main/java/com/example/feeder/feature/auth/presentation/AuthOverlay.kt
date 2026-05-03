package com.example.feeder.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AuthOverlay (
    onCLose: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onLoginClick: (email: String, password: String) -> Unit,
    onRegisterClick: (email: String, username: String, password: String, OnSuccess: () -> Unit) -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f))
    )
    {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "X",
                        modifier = Modifier.clickable { onCLose() },
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                when (authMode) {
                    AuthMode.LOGIN -> {
                        LoginContent(
                            onForgotPasswordClick = onForgotPasswordClick,
                            onGoogleSignInClick = onGoogleSignInClick,
                            onLoginClick = onLoginClick,
                            onGoToRegister = { authMode = AuthMode.REGISTER }
                        )
                    }

                    AuthMode.REGISTER -> {
                        RegisterContent(
                            onRegisterClick = { email, username, password ->
                                onRegisterClick(email, username, password) {
                                    authMode = AuthMode.LOGIN
                                }
                            },
                            onGoToLogin = { authMode = AuthMode.LOGIN }
                        )
                    }
                }
            }
        }
    }

}