package com.example.feeder.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feeder.feature.auth.data.AuthSession

@Composable
fun ProfileContent (
    session: AuthSession?,
    onSignOutClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        session?.let {
            Text(
                text = it.username
            )

            Text(
                text = it.email,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Button(
            onClick = {
                if (session != null) {
                    onSignOutClick()
                } else {
                    onSignInClick()
                }
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(
                text = if (session != null) {
                    "Sign out"
                } else {
                    "Sign in"
                }
            )

        }

    }
}