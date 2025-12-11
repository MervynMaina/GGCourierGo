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
import com.mervyn.ggcouriergo.navigation.ROUT_LOGIN
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
import com.mervyn.ggcouriergo.repository.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(AuthRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("driver") } // default

    Scaffold(
        topBar = { TopAppBar(title = { Text("Register") }) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(Modifier.height(8.dp))

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

            Text("Select Role:", style = MaterialTheme.typography.labelLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = role == "driver",
                    onClick = { role = "driver" }
                )
                Text("Driver")

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = role == "dispatcher",
                    onClick = { role = "dispatcher" }
                )
                Text("Dispatcher")

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = role == "admin",
                    onClick = { role = "admin" }
                )
                Text("Admin")
            }

            Spacer(Modifier.height(16.dp))

            when (uiState) {
                is RegisterUIState.Loading ->
                    CircularProgressIndicator()

                is RegisterUIState.Error ->
                    Text(
                        (uiState as RegisterUIState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )

                else -> {}
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.register(name, email, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState != RegisterUIState.Loading
            ) {
                Text("Create account")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate(ROUT_LOGIN) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Already have an account? Login")
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is RegisterUIState.Success) {
            navController.navigate(ROUT_LOGIN) {
                popUpTo("register") { inclusive = true }
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    CourierGoTheme {
        RegisterScreen(navController = rememberNavController())
    }
}
