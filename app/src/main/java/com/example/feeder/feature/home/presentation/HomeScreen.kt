package com.example.feeder.feature.home.presentation

import android.R.attr.text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.feeder.feature.auth.presentation.AuthOverlay

@Composable
fun HomeScreen() {
    val showAuthOverlay = remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "HomeScreen",
            modifier = Modifier.align(Alignment.Center)
        )
        if (showAuthOverlay.value) {
            AuthOverlay(
                onCLose = { showAuthOverlay.value = false },
                onForgotPasswordClick = {},
                onGoogleSignInClick = {},
                onLoginClick = { _, _ -> },
                onRegisterClick = { _, _, _ -> }
            )
        }

    }
}