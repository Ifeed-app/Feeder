package com.example.feeder.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feeder.data.remote.AuthRemoteDataSource
import com.example.feeder.feature.auth.data.AuthSession
import com.example.feeder.feature.auth.data.AuthSessionManager
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginValidationErrors(
    val email: String? = null,
    val password: String? = null
) {
    val hasErrors: Boolean
        get() = email != null || password != null
}

data class RegisterValidationErrors(
    val email: String? = null,
    val username: String? = null,
    val password: String? = null
) {
    val hasErrors: Boolean
        get() = email != null || username != null || password != null
}

class AuthViewModel (    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionManager: AuthSessionManager
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginValidationErrors =
        MutableStateFlow(LoginValidationErrors())

    val loginValidationErrors: StateFlow<LoginValidationErrors> =
        _loginValidationErrors.asStateFlow()

    private val _registerValidationErrors =
        MutableStateFlow(RegisterValidationErrors())

    val registerValidationErrors: StateFlow<RegisterValidationErrors> =
        _registerValidationErrors.asStateFlow()

    fun getCurrentSession(): AuthSession? {
        return sessionManager.getSession()
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (AuthSession) -> Unit,
        onError: (String) -> Unit
    ) {
        val normalizedEmail = email.trim()
        if (!validateLoginForm(normalizedEmail, password)) {
            return
        }
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authRemoteDataSource.signIn(
                    email = normalizedEmail,
                    password = password
                )

                val session = AuthSession(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    userId = response.userInfo.id,
                    email = response.userInfo.email,
                    username = response.userInfo.username
                )

                sessionManager.saveSession(session)
                _loginValidationErrors.value = LoginValidationErrors()
                onSuccess(session)
            } catch (exception: Exception) {
                exception.printStackTrace()

                onError(
                    mapGrpcError(
                        exception = exception,
                        defaultMessage = "Sign in failed. Please try again!"
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(
        email: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val normalizedEmail = email.trim()
        val normalizedUsername = username.trim()
        if (!validateRegisterForm(
                email = normalizedEmail,
                username = normalizedUsername,
                password = password
            )
        ) {
            return
        }
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRemoteDataSource.signUp(
                    email = normalizedEmail,
                    username = username,
                    password = password
                )
                _registerValidationErrors.value = RegisterValidationErrors()
                onSuccess()
            } catch (exception: Exception) {
                exception.printStackTrace()

                onError(
                 mapGrpcError(
                     exception = exception,
                     defaultMessage = "Registration failed. Please try again!"
                 )
                )
            } finally {
                _isLoading.value = false
            }
        }

    }

    fun signOut() {
        sessionManager.clearSession()
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "Email is required."
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Enter a valid email address."
        }

        return null
    }

    private fun validateUsername(username: String): String? {
        if (username.isBlank()) {
            return "Username is required."
        }

        if (username.length < 3) {
            return "Username must contain at least 3 characters."
        }

        if (username.length > 30) {
            return "Username must contain at most 30 characters."
        }

        return null
    }

    private fun validatePassword(password: String): String? {
        if (password.isBlank()) {
            return "Password is required."
        }

        if (password.length < 8) {
            return "Password must contain at least 8 characters."
        }

        return null
    }

    private fun validateLoginForm(
        email: String,
        password: String
    ): Boolean {
        val errors = LoginValidationErrors(
            email = validateEmail(email),
            password = validatePassword(password)
        )

        _loginValidationErrors.value = errors

        return !errors.hasErrors
    }

    private fun validateRegisterForm(
        email: String,
        username: String,
        password: String
    ): Boolean {
        val errors = RegisterValidationErrors(
            email = validateEmail(email),
            username = validateUsername(username),
            password = validatePassword(password)
        )

        _registerValidationErrors.value = errors

        return !errors.hasErrors
    }

    private fun mapGrpcError(
        exception: Exception,
        defaultMessage: String
    ): String {
        val status = when (exception) {
            is StatusRuntimeException -> exception.status
            is StatusException -> exception.status
            else -> return defaultMessage
        }
        return when (status.code) {
            Status.Code.INVALID_ARGUMENT ->
                status.description ?: "Some entered information is invalid."

            Status.Code.UNAUTHENTICATED ->
                "Email or password is incorrect."

            Status.Code.ALREADY_EXISTS ->
                "An account with this email or username already exists."

            Status.Code.NOT_FOUND ->
                "The requested account could not be found."

            Status.Code.PERMISSION_DENIED ->
                "You do not have permission to perform this action."

            Status.Code.UNAVAILABLE ->
                "Unable to connect to the server. Check your connection and try again."

            Status.Code.DEADLINE_EXCEEDED ->
                "The server took too long to respond. Please try again."

            Status.Code.INTERNAL ->
                "A server error occurred. Please try again."

            else ->
                status.description ?: defaultMessage

        }


    }
}