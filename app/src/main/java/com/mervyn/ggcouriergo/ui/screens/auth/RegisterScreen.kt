package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.mervyn.ggcouriergo.data.RegisterViewModel
import com.mervyn.ggcouriergo.data.RegisterViewModelFactory
import com.mervyn.ggcouriergo.models.RegisterUIState
import com.mervyn.ggcouriergo.repository.AuthRepository
import com.mervyn.ggcouriergo.ui.components.GGCourierLogo
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

private val ROLES = listOf("driver", "dispatcher", "admin")

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
    var role by remember { mutableStateOf(ROLES.first()) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Standardized text field colors for high visibility on white surface
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,            // Ensures input is black, not gray
        unfocusedTextColor = Color.Black,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.DarkGray,      // Darker gray for better label visibility
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = Color.Gray
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column {
            Spacer(Modifier.height(40.dp))
            Row(
                modifier = Modifier.padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GGCourierLogo(modifier = Modifier.size(40.dp), color = Color.White)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "JOIN THE TEAM",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 20.sp
                )
            }
            Spacer(Modifier.height(30.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Fill in your details below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(32.dp))

                    // FULL NAME
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(Modifier.height(16.dp))

                    // EMAIL
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

                    // PASSWORD
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
                                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors
                    )

                    Spacer(Modifier.height(24.dp))

                    // ROLE SELECTION
                    Text(
                        text = "Select Your Role",
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ROLES.forEach { r ->
                            FilterChip(
                                selected = role == r,
                                onClick = { role = r },
                                label = {
                                    Text(
                                        r.replaceFirstChar { it.uppercase() },
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.Transparent,
                                    labelColor = MaterialTheme.colorScheme.primary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    enabled = true,
                                    selected = role == r
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // LOADING & ERROR FEEDBACK
                    if (uiState is RegisterUIState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else if (uiState is RegisterUIState.Error) {
                        Text(
                            text = (uiState as RegisterUIState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // SUBMIT BUTTON
                    Button(
                        onClick = { viewModel.register(name, email, password, role) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("SIGN UP", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }

                    TextButton(onClick = { navController.navigate("login") }) {
                        Text(
                            "Already have an account? Login",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is RegisterUIState.Success) {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    GGCourierGoTheme {
        RegisterScreen(navController = rememberNavController())
    }
}