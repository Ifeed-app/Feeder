package com.example.feeder.feature.auth.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
fun AuthOverlay(
    isLoading: Boolean,
    loginValidationErrors: LoginValidationErrors,
    registerValidationErrors: RegisterValidationErrors,
    onCLose: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onLoginClick: (
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) -> Unit,
    onRegisterClick: (
        email: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }

    var resultType by remember { mutableStateOf<AuthResultType?>(null) }
    var resultTitle by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }
    var closeOnSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF111827).copy(alpha = 0.72f),
            border = BorderStroke(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.45f)
            ),
            tonalElevation = 8.dp,
            shadowElevation = 12.dp
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
                    IconButton(onClick = onCLose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                val currentResultType = resultType

                if (currentResultType != null) {
                    AuthResultDialog (
                        type = currentResultType,
                        title = resultTitle,
                        message = resultMessage,
                        buttonText = if (currentResultType == AuthResultType.SUCCESS) {
                            if (closeOnSuccess) "Continue" else "Go to sign in"
                        } else {
                            "Try again"
                        },
                        onDismiss = {
                            if (currentResultType == AuthResultType.SUCCESS) {
                                resultType = null
                                resultTitle = ""
                                resultMessage = ""

                                if (closeOnSuccess) {
                                    onCLose()
                                } else {
                                    authMode = AuthMode.LOGIN
                                }
                            } else {
                                resultType = null
                                resultTitle = ""
                                resultMessage = ""
                            }

                            closeOnSuccess = false
                        }
                    )
                } else {
                    when (authMode) {
                        AuthMode.LOGIN -> {
                            LoginContent(
                                onForgotPasswordClick = onForgotPasswordClick,
                                onGoogleSignInClick = onGoogleSignInClick,
                                onLoginClick = { email, password ->
                                    onLoginClick(
                                        email,
                                        password,
                                        {
                                            closeOnSuccess = true
                                            resultType = AuthResultType.SUCCESS
                                            resultTitle = "Signed in"
                                            resultMessage = "You have successfully signed in."
                                        },
                                        { errorMessage ->
                                            resultType = AuthResultType.ERROR
                                            resultTitle = "Sign in failed"
                                            resultMessage = errorMessage
                                        }
                                    )
                                },
                                onGoToRegister = { authMode = AuthMode.REGISTER },
                                isLoading = isLoading,
                                emailError = loginValidationErrors.email,
                                passwordError = loginValidationErrors.password
                            )
                        }

                        AuthMode.REGISTER -> {
                            RegisterContent(
                                onRegisterClick = { email, username, password ->
                                    onRegisterClick(
                                        email,
                                        username,
                                        password,
                                        {
                                            closeOnSuccess = false
                                            resultType = AuthResultType.SUCCESS
                                            resultTitle = "Account created"
                                            resultMessage = "Your account has been created successfully. You can now sign in."
                                        },
                                        { errorMessage ->
                                            resultType = AuthResultType.ERROR
                                            resultTitle = "Registration failed"
                                            resultMessage = errorMessage
                                        }
                                    )
                                },
                                onGoToLogin = { authMode = AuthMode.LOGIN },
                                isLoading = isLoading,
                                emailError = registerValidationErrors.email,
                                usernameError = registerValidationErrors.username,
                                passwordError = registerValidationErrors.password
                            )
                        }
                    }
                }
            }
        }
    }
}