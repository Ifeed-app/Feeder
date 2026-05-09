package com.example.feeder.feature.home.presentation

import android.R.attr.text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.feeder.data.remote.AuthRemoteDataSource
import com.example.feeder.feature.auth.presentation.AuthOverlay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val showAuthOverlay = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val authRemoteDataSource = remember { AuthRemoteDataSource() }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Home / Feed screen",
            modifier = Modifier.align(Alignment.Center)
        )

        if (showAuthOverlay.value) {
            AuthOverlay(
                onCLose = { showAuthOverlay.value = false },
                onForgotPasswordClick = {},
                onGoogleSignInClick = {},
                onLoginClick = { _, _ -> },
                onRegisterClick = { email, username, password, onSuccess, onError ->
                    scope.launch {
                        try {
                            authRemoteDataSource.signUp(
                                email = email,
                                username = username,
                                password = password
                            )

                            onSuccess()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            onError(e.message ?: "Registration failed. Please try again.")
                        }
                    }
                }
            )
        }
    }
}