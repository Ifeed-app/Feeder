package com.example.feeder.feature.home.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feeder.core.network.GrpcChannelProvider
import com.example.feeder.core.network.GrpcConfig
import com.example.feeder.core.network.JwtClientInterceptor
import com.example.feeder.data.remote.AuthRemoteDataSource
import com.example.feeder.data.remote.ContentRemoteDataSource
import com.example.feeder.feature.auth.data.AuthSessionManager
import com.example.feeder.feature.auth.presentation.AuthOverlay
import com.example.feeder.feature.auth.presentation.AuthViewModel
import com.example.feeder.feature.auth.presentation.AuthViewModelFactory
import com.example.feeder.feature.feed.presentation.FeedContent
import com.example.feeder.feature.feed.presentation.FeedViewModel
import com.example.feeder.feature.feed.presentation.FeedViewModelFactory
import com.example.feeder.feature.interaction.data.InteractionRemoteDataSource
import com.example.feeder.feature.interaction.presentation.InteractionViewModel
import com.example.feeder.feature.interaction.presentation.InteractionViewModelFactory
import com.example.feeder.data.remote.RecommendationRemoteDataSource

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val sessionManager = remember { AuthSessionManager(context.applicationContext) }
    val authRemoteDataSource = remember { AuthRemoteDataSource() }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            authRemoteDataSource = authRemoteDataSource,
            sessionManager = sessionManager
        )
    )
    val contentRemoteDataSource = remember {
        ContentRemoteDataSource()
    }

    val recommendationRemoteDataSource = remember {
        RecommendationRemoteDataSource()
    }

    val feedViewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            contentRemoteDataSource = contentRemoteDataSource,
            recommendationRemoteDataSource = recommendationRemoteDataSource,
            authSessionManager = sessionManager
        )
    )
    val authIsLoading by authViewModel.isLoading.collectAsState()
    val loginValidationErrors by
    authViewModel.loginValidationErrors.collectAsState()
    val registerValidationErrors by
    authViewModel.registerValidationErrors.collectAsState()
    val feedUiState by feedViewModel.uiState.collectAsState()
    var currentSession by remember {
        mutableStateOf(authViewModel.getCurrentSession())
    }
    LaunchedEffect(currentSession?.userId) {
        if (currentSession != null) {
            feedViewModel.loadFeed()
        }
    }
    var showAuthOverlay by remember { mutableStateOf(currentSession == null) }
    var selectedTab by remember {
        mutableStateOf(MainTab.HOME)
    }
    val jwtInterceptor = remember {
        JwtClientInterceptor {
            sessionManager
                .getSession()
                ?.accessToken
        }
    }

    val interactionChannel = remember {
        GrpcChannelProvider.provideChannel(
            GrpcConfig.INTERACTION_SERVICE_PORT,
            jwtInterceptor
        )
    }

    val interactionRemoteDataSource = remember {
        InteractionRemoteDataSource(
            channel = interactionChannel
        )
    }

    val interactionViewModel: InteractionViewModel = viewModel(
        factory = InteractionViewModelFactory(
            interactionRemoteDataSource =
                interactionRemoteDataSource,
            userIdProvider = {
                authViewModel
                    .getCurrentSession()
                    ?.userId
            }
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black
            ) {
                NavigationBarItem(
                    selected = selectedTab == MainTab.HOME,
                    onClick = {
                        selectedTab = MainTab.HOME
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == MainTab.PROFILE,
                    onClick = {
                        selectedTab = MainTab.PROFILE
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text("Profile")
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.HOME -> {
                    when {
                        feedUiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        feedUiState.errorMessage != null -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = feedUiState.errorMessage
                                        ?: "Failed to load feed."
                                )

                                TextButton(
                                    onClick = feedViewModel::loadFeed
                                ) {
                                    Text("Retry")
                                }
                            }
                        }

                        feedUiState.videos.isEmpty() -> {
                            Text(
                                text = "No videos available.",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            FeedContent(
                                videos = feedUiState.videos,
                                isLoadingMore = feedUiState.isLoadingMore,
                                debugMessage =  feedUiState.debugMessage,
                                interactionViewModel = interactionViewModel,
                                onLoadMore = {}
                            )
                        }
                    }
                }

                MainTab.PROFILE -> {
                    ProfileContent(
                        session = currentSession,
                        onSignOutClick = {
                            authViewModel.signOut()
                            currentSession = null
                            selectedTab = MainTab.HOME
                            showAuthOverlay = true
                        },
                        onSignInClick = {
                            showAuthOverlay = true
                        }
                    )
                }
            }

            if (showAuthOverlay) {
                AuthOverlay(
                    isLoading = authIsLoading,
                    loginValidationErrors = loginValidationErrors,
                    registerValidationErrors = registerValidationErrors,
                    onClose = {
                        showAuthOverlay = false
                    },
                    onForgotPasswordClick = {},
                    onGoogleSignInClick = {},
                    onLoginClick = {
                            identifier,
                            password,
                            onSuccess,
                            onError ->

                        authViewModel.signIn(
                            identifier = identifier ,
                            password = password,
                            onSuccess = { session ->
                                currentSession = session
                                onSuccess()
                            },
                            onError = onError
                        )
                    },
                    onRegisterClick = {
                            email,
                            username,
                            password,
                            birthYear,
                            gender,
                            countryCode,
                            preferredLanguage,
                            selectedInterests,
                            onSuccess,
                            onError ->

                        authViewModel.signUp(
                            email = email,
                            username = username,
                            password = password,
                            birthYear = birthYear,
                            gender = gender,
                            countryCode = countryCode,
                            preferredLanguage = preferredLanguage,
                            interests = selectedInterests,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    }
                )
            }
        }
    }
}