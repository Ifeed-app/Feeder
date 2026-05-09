package com.example.feeder.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.feeder.data.remote.AuthRemoteDataSource
import kotlinx.coroutines.launch

@Composable
fun AuthScreen() {
    val scope = rememberCoroutineScope()
    val remoteDataSource = remember { AuthRemoteDataSource() }
    var resultText by remember { mutableStateOf("No request yet") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = resultText)
        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = remoteDataSource.signUp(
                            email = "mobiletest@example.com",
                            username = "mobiletest",
                            password = "password123"
                        )
                        resultText = response.userInfo.email
                    } catch (e: Exception) {
                        resultText = e.message ?: "Unknown Error"
                    }
                }
            }
        )
        {
          Text("Test Sign up")
        }
    }
}