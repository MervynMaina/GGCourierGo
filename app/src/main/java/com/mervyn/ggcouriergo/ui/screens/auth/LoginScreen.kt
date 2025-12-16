package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.LoginViewModel
import com.mervyn.ggcouriergo.data.LoginViewModelFactory
import com.mervyn.ggcouriergo.models.LoginUIState
import com.mervyn.ggcouriergo.navigation.ROUT_MAIN_APP
import com.mervyn.ggcouriergo.repository.AuthRepository
import com.mervyn.ggcouriergo.ui.components.GGCourierLogo
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import com.mervyn.ggcouriergo.ui.theme.GGColors

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

    // CRITICAL: Reusable colors to ensure visibility on white background
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.DarkGray,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
    ) {
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

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxSize(),
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
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(32.dp))

                    // EMAIL FIELD
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textFieldColors // Use the high-visibility colors
                    )

                    Spacer(Modifier.height(16.dp))

                    // PASSWORD FIELD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors // FIXED: Use the same colors here!
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
                            Text("Remember Me", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                        }

                        TextButton(onClick = { viewModel.sendPasswordReset(email) }) {
                            Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Status Feedback
                    when (val state = uiState) {
                        is LoginUIState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        is LoginUIState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        is LoginUIState.PasswordResetSent -> Text("Reset link sent to your email!", color = Color(0xFF4CAF50))
                        else -> {}
                    }

                    Spacer(Modifier.height(16.dp))

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
                        Text("Don't have an account? Register Now", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is LoginUIState.SuccessAdmin || uiState is LoginUIState.SuccessDispatcher || uiState is LoginUIState.SuccessDriver) {
            navController.navigate(ROUT_MAIN_APP) { popUpTo("login") { inclusive = true } }
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