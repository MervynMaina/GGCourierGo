package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.LoginViewModel
import com.mervyn.ggcouriergo.data.LoginViewModelFactory
import com.mervyn.ggcouriergo.data.ThemeSettings
import com.mervyn.ggcouriergo.models.LoginUIState
import com.mervyn.ggcouriergo.navigation.ROUT_MAIN_APP
import com.mervyn.ggcouriergo.repository.AuthRepository
import com.mervyn.ggcouriergo.ui.components.GGCourierLogo
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(AuthRepository()))
) {
    val uiState by viewModel.uiState.collectAsState(initial = LoginUIState.Idle)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Theme logic for the shortcut toggle
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeSettings = remember { ThemeSettings(context) }
    val isDarkMode by themeSettings.darkModeFlow.collectAsState(initial = false)

    // THEME AWARE colors for TextFields
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // --- 1. THEME TOGGLE SHORTCUT (Top Right) ---
        IconButton(
            onClick = { scope.launch { themeSettings.toggleTheme() } },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = Color.White // Keep white to stand out against primary green background
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(60.dp))
            GGCourierLogo(modifier = Modifier.size(80.dp), color = Color.White)
            Text(
                text = "GREEN GIANT",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
            Spacer(Modifier.height(40.dp))

            // Main Form Container
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface // Theme aware: White in light, Dark in dark
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sign in to continue delivery",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(32.dp))

                    // EMAIL FIELD
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textFieldColors
                    )

                    Spacer(Modifier.height(16.dp))

                    // PASSWORD FIELD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Remember Me", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        TextButton(onClick = { viewModel.sendPasswordReset(email) }) {
                            Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Status Feedback
                    when (val state = uiState) {
                        is LoginUIState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        is LoginUIState.Error -> Text(
                            state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is LoginUIState.PasswordResetSent -> Text("Reset link sent!", color = Color(0xFF4CAF50))
                        else -> {}
                    }

                    Spacer(Modifier.height(16.dp))

                    // LOGIN BUTTON
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("LOGIN", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    TextButton(onClick = { navController.navigate("register") }) {
                        Text(
                            "Don't have an account? Register Now",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUIState.SuccessAdmin,
            is LoginUIState.SuccessDispatcher,
            is LoginUIState.SuccessDriver -> {
                navController.navigate(ROUT_MAIN_APP) {
                    popUpTo("login") { inclusive = true }
                }
            }
            is LoginUIState.SuccessCustomer -> {
                navController.navigate("user_dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            }
            else -> {}
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    GGCourierGoTheme {
        LoginScreen(navController = rememberNavController())
    }
}