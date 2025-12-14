package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.LoginViewModel
import com.mervyn.ggcouriergo.data.LoginViewModelFactory
import com.mervyn.ggcouriergo.models.LoginUIState
import com.mervyn.ggcouriergo.repository.AuthRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import com.mervyn.ggcouriergo.ui.theme.GGColors
import com.mervyn.ggcouriergo.navigation.ROUT_MAIN_APP // NEW: Import the centralized route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(AuthRepository()))
) {
    // Collect UI state
    val uiState: LoginUIState by viewModel.uiState.collectAsState(initial = LoginUIState.Idle)

    // Local State for Inputs and UX features
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // --- Email Field ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(16.dp))

            // --- Password Field (with Visibility Toggle) ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            // --- Remember Me & Forgot Password Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Remember Me Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = {
                            rememberMe = it
                            // TODO: Integrate DataStore/SharedPreferences persistence logic here
                        },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text("Remember Me", style = MaterialTheme.typography.bodySmall)
                }

                // Forgot Password Button
                TextButton(onClick = {
                    viewModel.sendPasswordReset(email)
                }) {
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.secondary)
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- State Indicators and Error/Success Messages ---
            when (val state = uiState) {
                is LoginUIState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                is LoginUIState.Error -> {
                    Text("Login Failed: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is LoginUIState.PasswordResetSent -> {
                    Text("Password reset link sent to $email!", color = GGColors.SuccessGreen, fontWeight = FontWeight.Bold)
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.resetStateToIdle()
                    }
                }
                is LoginUIState.PasswordResetError -> {
                    Text("Reset Failed: ${state.message}", color = MaterialTheme.colorScheme.error)
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.resetStateToIdle()
                    }
                }
                else -> {}
            }

            Spacer(Modifier.height(16.dp))

            // --- Login Button ---
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState != LoginUIState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign In", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))

            // --- Register Navigation ---
            TextButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Don't have an account? Register", color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    // Navigate based on role (Crucial for correct dashboard routing)
    LaunchedEffect(uiState) {
        when (uiState) {
            // FIX: UNIFY all successful login states to navigate to the MainAppScaffold
            is LoginUIState.SuccessAdmin,
            is LoginUIState.SuccessDispatcher,
            is LoginUIState.SuccessDriver -> {
                navController.navigate(ROUT_MAIN_APP) {
                    // Clear the back stack entirely, preventing navigation back to Login/Splash
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