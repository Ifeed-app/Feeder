package com.example.feeder.feature.auth.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
    onClose: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onLoginClick: (
        identifier: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) -> Unit,
    onRegisterClick: (
        email: String,
        username: String,
        password: String,
        birthYear: Int,
        gender: String,
        countryCode: String,
        preferredLanguage: String,
        selectedInterests: List<String>,
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
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 32.dp)
                .heightIn(max = 760.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF111318),
            border = BorderStroke(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f)
            ),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 12.dp,
                        bottom = 24.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.72f)
                        )
                    }
                }

                val currentResultType = resultType

                if (currentResultType != null) {
                    AuthResultDialog(
                        type = currentResultType,
                        title = resultTitle,
                        message = resultMessage,
                        buttonText = if (
                            currentResultType == AuthResultType.SUCCESS
                        ) {
                            if (closeOnSuccess) {
                                "Continue"
                            } else {
                                "Go to sign in"
                            }
                        } else {
                            "Try again"
                        },
                        onDismiss = {
                            resultType = null
                            resultTitle = ""
                            resultMessage = ""

                            if (
                                currentResultType == AuthResultType.SUCCESS
                            ) {
                                if (closeOnSuccess) {
                                    onClose()
                                } else {
                                    authMode = AuthMode.LOGIN
                                }
                            }

                            closeOnSuccess = false
                        }
                    )
                } else {
                    when (authMode) {
                        AuthMode.LOGIN -> {
                            LoginContent(
                                onForgotPasswordClick =
                                    onForgotPasswordClick,
                                onGoogleSignInClick =
                                    onGoogleSignInClick,
                                onLoginClick = {
                                        identifier,
                                        password ->

                                    onLoginClick(
                                        identifier,
                                        password,
                                        {
                                            closeOnSuccess = true
                                            resultType =
                                                AuthResultType.SUCCESS
                                            resultTitle = "Signed in"
                                            resultMessage =
                                                "You have successfully signed in."
                                        },
                                        { errorMessage ->
                                            resultType =
                                                AuthResultType.ERROR
                                            resultTitle =
                                                "Sign in failed"
                                            resultMessage =
                                                errorMessage
                                        }
                                    )
                                },
                                onGoToRegister = {
                                    authMode = AuthMode.REGISTER
                                },
                                isLoading = isLoading,
                                identifierError =
                                    loginValidationErrors.identifier,
                                passwordError =
                                    loginValidationErrors.password
                            )
                        }

                        AuthMode.REGISTER -> {
                            RegisterContent(
                                onRegisterClick = {
                                        email,
                                        username,
                                        password,
                                        birthYear,
                                        gender,
                                        countryCode,
                                        preferredLanguage,
                                        selectedInterests ->

                                    onRegisterClick(
                                        email,
                                        username,
                                        password,
                                        birthYear,
                                        gender,
                                        countryCode,
                                        preferredLanguage,
                                        selectedInterests,
                                        {
                                            closeOnSuccess = false
                                            resultType =
                                                AuthResultType.SUCCESS
                                            resultTitle =
                                                "Account created"
                                            resultMessage =
                                                "Your account has been created. Please verify your email before signing in."
                                        },
                                        { errorMessage ->
                                            resultType =
                                                AuthResultType.ERROR
                                            resultTitle =
                                                "Registration failed"
                                            resultMessage =
                                                errorMessage
                                        }
                                    )
                                },
                                onGoToLogin = {
                                    authMode = AuthMode.LOGIN
                                },
                                isLoading = isLoading,
                                emailError =
                                    registerValidationErrors.email,
                                usernameError =
                                    registerValidationErrors.username,
                                passwordError =
                                    registerValidationErrors.password
                            )
                        }
                    }
                }
            }
        }
    }
}