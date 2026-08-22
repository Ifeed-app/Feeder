package com.example.feeder.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.Collator
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val RegisterAccent = Color(0xFF7C5CFC)
private val RegisterText = Color(0xFFF8FAFC)
private val RegisterMutedText = Color(0xFF9CA3AF)

private data class CountryOption(
    val name: String,
    val code: String
)

private data class LanguageOption(
    val name: String,
    val code: String
)

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)
@Composable
fun RegisterContent(
    onRegisterClick: (
        email: String,
        username: String,
        password: String,
        birthYear: Int,
        gender: String,
        countryCode: String,
        preferredLanguage: String,
        selectedInterests: List<String>
    ) -> Unit,
    onGoToLogin: () -> Unit,
    isLoading: Boolean,
    emailError: String?,
    usernameError: String?,
    passwordError: String?,
) {
    var currentStep by remember { mutableStateOf(0) }

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var selectedBirthDateMillis by remember {
        mutableStateOf<Long?>(null)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var selectedGender by remember {
        mutableStateOf("")
    }

    var selectedCountry by remember {
        mutableStateOf<CountryOption?>(null)
    }

    var selectedLanguage by remember {
        mutableStateOf<LanguageOption?>(null)
    }

    val selectedInterests = remember {
        mutableStateListOf<String>()
    }

    val countries = remember {
        createCountryOptions()
    }

    val languages = remember {
        listOf(
            LanguageOption("Croatian", "hr"),
            LanguageOption("English", "en"),
            LanguageOption("German", "de"),
            LanguageOption("Italian", "it"),
            LanguageOption("Spanish", "es"),
            LanguageOption("French", "fr"),
            LanguageOption("Slovenian", "sl"),
            LanguageOption("Serbian", "sr"),
            LanguageOption("Bosnian", "bs")
        )
    }

    val genderOptions = remember {
        listOf(
            "Male",
            "Female",
            "Other",
            "Prefer not to say"
        )
    }

    val interestOptions = remember {
        listOf(
            "Fitness",
            "Food",
            "Travel",
            "Nature",
            "Cars",
            "Technology",
            "Music",
            "Gaming",
            "Sports",
            "Fashion",
            "Movies",
            "Education"
        )
    }

    val selectedBirthDate = selectedBirthDateMillis?.let { millis ->
        Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
    }

    val formattedBirthDate = selectedBirthDate?.format(
        DateTimeFormatter.ofPattern("dd.MM.yyyy.")
    ).orEmpty()

    val birthYear = selectedBirthDate?.year ?: 0

    val isCurrentStepComplete = when (currentStep) {
        0 -> {
            email.isNotBlank() &&
                    username.isNotBlank() &&
                    password.isNotBlank()
        }

        1 -> {
            selectedBirthDateMillis != null &&
                    selectedGender.isNotBlank() &&
                    selectedCountry != null &&
                    selectedLanguage != null
        }

        2 -> selectedInterests.isNotEmpty()

        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RegistrationHeader(
            currentStep = currentStep
        )

        when (currentStep) {
            0 -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Email")
                        },
                        singleLine = true,
                        isError = emailError != null,
                        supportingText = {
                            emailError?.let {
                                Text(it)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = registerTextFieldColors()
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Username")
                        },
                        singleLine = true,
                        isError = usernameError != null,
                        supportingText = {
                            usernameError?.let {
                                Text(it)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = registerTextFieldColors()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Password")
                        },
                        singleLine = true,
                        isError = passwordError != null,
                        supportingText = {
                            passwordError?.let {
                                Text(it)
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (passwordVisible) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    },
                                    tint = RegisterMutedText
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = registerTextFieldColors()
                    )
                }
            }

            1 -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SelectionButton(
                        value = formattedBirthDate,
                        placeholder = "Date of birth",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showDatePicker = true
                        }
                    )

                    DropdownSelector(
                        label = "Gender",
                        selectedValue = selectedGender,
                        options = genderOptions,
                        optionLabel = { it },
                        onOptionSelected = {
                            selectedGender = it
                        }
                    )

                    DropdownSelector(
                        label = "Country",
                        selectedValue =
                            selectedCountry?.name.orEmpty(),
                        options = countries,
                        optionLabel = { country ->
                            country.name
                        },
                        onOptionSelected = { country ->
                            selectedCountry = country
                        }
                    )

                    DropdownSelector(
                        label = "Preferred language",
                        selectedValue =
                            selectedLanguage?.name.orEmpty(),
                        options = languages,
                        optionLabel = { language ->
                            language.name
                        },
                        onOptionSelected = { language ->
                            selectedLanguage = language
                        }
                    )
                }
            }

            2 -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "What are you interested in?",
                        color = RegisterText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Choose at least one category. These interests will help personalize your feed.",
                        color = RegisterMutedText,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        interestOptions.forEach { interest ->
                            val normalizedInterest =
                                interest.lowercase()

                            val isSelected =
                                normalizedInterest in selectedInterests

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        selectedInterests.remove(
                                            normalizedInterest
                                        )
                                    } else {
                                        selectedInterests.add(
                                            normalizedInterest
                                        )
                                    }
                                },
                                label = {
                                    Text(interest)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    labelColor = RegisterMutedText,
                                    selectedContainerColor =
                                        RegisterAccent.copy(alpha = 0.22f),
                                    selectedLabelColor = RegisterText
                                )
                            )
                        }
                    }

                    Text(
                        text = "${selectedInterests.size} selected",
                        color = RegisterMutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        RegistrationNavigation(
            currentStep = currentStep,
            isLoading = isLoading,
            isCurrentStepComplete = isCurrentStepComplete,
            onBack = {
                if (currentStep > 0) {
                    currentStep--
                }
            },
            onContinue = {
                if (currentStep < 2) {
                    currentStep++
                } else {
                    onRegisterClick(
                        email.trim(),
                        username.trim(),
                        password,
                        birthYear,
                        normalizeGender(selectedGender),
                        selectedCountry?.code.orEmpty(),
                        selectedLanguage?.code.orEmpty(),
                        selectedInterests.toList()
                    )
                }
            }
        )

        TextButton(
            onClick = onGoToLogin,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            ),
            enabled = !isLoading
        ) {
            Text(
                text = "Already have an account? ",
                color = RegisterMutedText
            )

            Text(
                text = "Sign in",
                color = RegisterAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis =
                selectedBirthDateMillis,
            selectableDates = PastDateSelectableDates
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedBirthDateMillis =
                            datePickerState.selectedDateMillis

                        showDatePicker = false
                    },
                    enabled =
                        datePickerState.selectedDateMillis != null
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@Composable
private fun RegistrationHeader(
    currentStep: Int
) {
    val stepTitles = listOf(
        "Account",
        "Personal info",
        "Interests"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Create account",
            color = RegisterText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Step ${currentStep + 1} of ${stepTitles.size} · ${stepTitles[currentStep]}",
            color = RegisterMutedText,
            style = MaterialTheme.typography.bodyMedium
        )

        LinearProgressIndicator(
            progress = {
                (currentStep + 1) / stepTitles.size.toFloat()
            },
            modifier = Modifier.fillMaxWidth(),
            color = RegisterAccent,
            trackColor = Color.White.copy(alpha = 0.10f)
        )
    }
}

@Composable
private fun RegistrationNavigation(
    currentStep: Int,
    isLoading: Boolean,
    isCurrentStepComplete: Boolean,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back")
            }
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            enabled = !isLoading && isCurrentStepComplete,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RegisterAccent,
                contentColor = Color.White
            )
        ) {
            if (isLoading && currentStep == 2) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = if (currentStep < 2) {
                        "Continue"
                    } else {
                        "Create account"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SelectionButton(
    value: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (leadingIcon != null) {
            leadingIcon()

            Spacer(
                modifier = Modifier.size(10.dp)
            )
        }

        Text(
            text = value.ifBlank {
                placeholder
            },
            modifier = Modifier.weight(1f),
            color = if (value.isBlank()) {
                RegisterMutedText
            } else {
                RegisterText
            }
        )
    }
}

@Composable
private fun <T> DropdownSelector(
    label: String,
    selectedValue: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = selectedValue.ifBlank {
                    label
                },
                modifier = Modifier.weight(1f),
                color = if (selectedValue.isBlank()) {
                    RegisterMutedText
                } else {
                    RegisterText
                }
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Open $label dropdown"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel(option)
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun registerTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = RegisterText,
        unfocusedTextColor = RegisterText,
        focusedLabelColor = RegisterAccent,
        unfocusedLabelColor = RegisterMutedText,
        focusedBorderColor = RegisterAccent,
        unfocusedBorderColor =
            Color.White.copy(alpha = 0.18f),
        cursorColor = RegisterAccent,
        errorBorderColor =
            MaterialTheme.colorScheme.error,
        errorLabelColor =
            MaterialTheme.colorScheme.error,
        errorSupportingTextColor =
            MaterialTheme.colorScheme.error
    )

private fun createCountryOptions(): List<CountryOption> {
    val displayLocale = Locale.ENGLISH
    val collator = Collator.getInstance(displayLocale)

    return Locale
        .getISOCountries()
        .map { countryCode ->
            val locale = Locale.Builder()
                .setRegion(countryCode)
                .build()

            CountryOption(
                name = locale.getDisplayCountry(displayLocale),
                code = countryCode
            )
        }
        .filter {
            it.name.isNotBlank()
        }
        .sortedWith { first, second ->
            collator.compare(first.name, second.name)
        }
}

private fun normalizeGender(
    gender: String
): String {
    return gender
        .trim()
        .uppercase()
        .replace(" ", "_")
}

@OptIn(ExperimentalMaterial3Api::class)
private object PastDateSelectableDates :
    androidx.compose.material3.SelectableDates {

    override fun isSelectableDate(
        utcTimeMillis: Long
    ): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }

    override fun isSelectableYear(
        year: Int
    ): Boolean {
        val currentYear = java.time.Year.now().value
        return year <= currentYear
    }
}