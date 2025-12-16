package com.mervyn.ggcouriergo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.mervyn.ggcouriergo.data.RegisterViewModel
import com.mervyn.ggcouriergo.data.RegisterViewModelFactory
import com.mervyn.ggcouriergo.data.ThemeSettings
import com.mervyn.ggcouriergo.models.RegisterUIState
import com.mervyn.ggcouriergo.repository.AuthRepository
import com.mervyn.ggcouriergo.ui.components.GGCourierLogo
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import kotlinx.coroutines.launch

private val ROLES = listOf("customer", "driver", "dispatcher", "admin")

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

    // Persistent Theme Logic
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
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                tint = Color.White
            )
        }

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

            // Main Form Container
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(32.dp))

                    // FULL NAME
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, null) },
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
                        leadingIcon = { Icon(Icons.Default.Email, null) },
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
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, null, tint = MaterialTheme.colorScheme.outline)
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

                    // FIXED FILTER CHIPS
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
                                    containerColor = Color.Transparent,
                                    labelColor = MaterialTheme.colorScheme.primary,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                ),
                                border = if (role == r) null else FilterChipDefaults.filterChipBorder(
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    enabled = true,
                                    selected = false
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // FEEDBACK
                    when (val state = uiState) {
                        is RegisterUIState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        is RegisterUIState.Error -> Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        else -> {}
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