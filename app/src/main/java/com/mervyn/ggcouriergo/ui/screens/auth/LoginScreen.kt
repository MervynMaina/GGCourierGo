package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mervyn.ggcouriergo.data.LoginViewModel
import com.mervyn.ggcouriergo.data.LoginViewModelFactory
import com.mervyn.ggcouriergo.models.LoginUIState
import com.mervyn.ggcouriergo.repository.AuthRepository
import com.mervyn.ggcouriergo.navigation.ROUT_ADMIN_DASHBOARD
import com.mervyn.ggcouriergo.navigation.ROUT_DRIVER_DASHBOARD
import com.mervyn.ggcouriergo.navigation.ROUT_DISPATCHER_DASHBOARD
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(AuthRepository()))
) {
    // Explicitly specify type for collectAsState
    val uiState: LoginUIState by viewModel.uiState.collectAsState(initial = LoginUIState.Idle)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Login") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(Modifier.height(16.dp))

            when (uiState) {
                is LoginUIState.Loading -> CircularProgressIndicator()
                is LoginUIState.Error -> Text(
                    (uiState as LoginUIState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                else -> {}
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState != LoginUIState.Loading
            ) {
                Text("Sign in with Email")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Don't have an account? Register")
            }
        }
    }

    // Navigate based on role
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUIState.SuccessDriver -> {
                navController.navigate(ROUT_DRIVER_DASHBOARD) { popUpTo("login") { inclusive = true } }
            }
            is LoginUIState.SuccessDispatcher -> {
                navController.navigate(ROUT_DISPATCHER_DASHBOARD) { popUpTo("login") { inclusive = true } }
            }
            is LoginUIState.SuccessAdmin -> {
                navController.navigate(ROUT_ADMIN_DASHBOARD) { popUpTo("login") { inclusive = true } }
            }
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        LoginScreen(navController)
    }
}
