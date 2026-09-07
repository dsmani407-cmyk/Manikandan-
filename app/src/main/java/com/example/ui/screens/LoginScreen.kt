package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.CrmViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class LoginTab {
    SIGN_IN,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LoginTab.SIGN_IN) }

    // Sign in fields
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Registration fields
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regTeamId by remember { mutableStateOf("team-1") }
    var regTeamName by remember { mutableStateOf("Alpha Warriors") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val teams by viewModel.teams.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
    val focusManager = LocalFocusManager.current

    fun performLogin() {
        if (userId.isBlank()) {
            errorMessage = "Please enter your registered Email or User ID"
            return
        }
        if (password.isBlank()) {
            errorMessage = "Please enter your password"
            return
        }
        isSubmitting = true
        errorMessage = null
        successMessage = null
        coroutineScope.launch {
            val (success, err) = viewModel.loginAsync(userId.trim(), password.trim())
            isSubmitting = false
            if (!success) {
                errorMessage = err ?: "Invalid credentials. Please verify your Email/User ID and password."
            }
        }
    }

    fun performRegistration() {
        if (regName.isBlank()) {
            errorMessage = "Please enter your full distributor name"
            return
        }
        if (regEmail.isBlank() || !regEmail.contains("@")) {
            errorMessage = "Please enter a valid email address"
            return
        }
        if (regPhone.isBlank()) {
            errorMessage = "Please enter your phone number"
            return
        }
        if (regPassword.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return
        }

        isSubmitting = true
        errorMessage = null
        successMessage = null
        coroutineScope.launch {
            val (success, err) = viewModel.registerDistributor(
                email = regEmail.trim(),
                pass = regPassword.trim(),
                name = regName.trim(),
                phone = regPhone.trim(),
                teamId = regTeamId,
                teamName = regTeamName
            )
            isSubmitting = false
            if (!success) {
                errorMessage = err ?: "Registration failed. Please try again."
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("login_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo & Header
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_smart_group_logo),
                    contentDescription = "Smart Group Logo",
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Smart Group",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Tranz India Authorized Dealer",
                fontSize = 13.sp,
                color = Amber400,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Central Supabase CRM • Multi-Device Portal",
                fontSize = 11.5.sp,
                color = Slate400,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Auth Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Central Database & RLS Security Status Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Emerald50,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Central Supabase Database Connected",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald700
                                )
                                Text(
                                    text = "Multi-device CRM with Row Level Security (RLS) data isolation",
                                    fontSize = 10.5.sp,
                                    color = Slate600
                                )
                            }
                        }
                    }

                    // Tabs: Sign In vs Register Distributor
                    TabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        containerColor = Slate100,
                        contentColor = Indigo600,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedTab == LoginTab.SIGN_IN,
                            onClick = {
                                selectedTab = LoginTab.SIGN_IN
                                errorMessage = null
                            },
                            text = {
                                Text(
                                    "Sign In",
                                    fontWeight = if (selectedTab == LoginTab.SIGN_IN) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == LoginTab.REGISTER,
                            onClick = {
                                selectedTab = LoginTab.REGISTER
                                errorMessage = null
                            },
                            text = {
                                Text(
                                    "Register Distributor",
                                    fontWeight = if (selectedTab == LoginTab.REGISTER) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }

                    // Error Alert
                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let { err ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Rose50)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = Rose600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = err,
                                        fontSize = 12.sp,
                                        color = Rose600,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (selectedTab == LoginTab.SIGN_IN) {
                        // Sign In Form
                        Text(
                            text = "Enter your individual credentials to access your phone's CRM session:",
                            fontSize = 11.5.sp,
                            color = Slate500
                        )

                        // User ID / Email Field
                        OutlinedTextField(
                            value = userId,
                            onValueChange = {
                                userId = it
                                errorMessage = null
                            },
                            label = { Text("Email or User ID") },
                            placeholder = { Text("e.g. distributor@smartgroup.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Indigo600)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_login_user_id"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            label = { Text("Password") },
                            placeholder = { Text("Enter account password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Indigo600)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Slate400
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_login_password"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    performLogin()
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Sign In Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                performLogin()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_login_submit"),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sign In",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Info note on security and isolation
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Distributor data is strictly isolated. Super Admin has full dashboard visibility.",
                                fontSize = 10.5.sp,
                                color = Slate500
                            )
                        }

                    } else {
                        // Registration Form
                        Text(
                            text = "Register a new distributor phone login. Account will sync to the central Supabase database:",
                            fontSize = 11.5.sp,
                            color = Slate500
                        )

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. Distributor Name") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Indigo600) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_name"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address (Login ID) *") },
                            placeholder = { Text("e.g. distributor@smartgroup.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Indigo600) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_email"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Phone Number *") },
                            placeholder = { Text("e.g. +91 98765 43210") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Indigo600) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_phone"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Create Password (min. 6 chars) *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Indigo600) },
                            trailingIcon = {
                                IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                    Icon(
                                        imageVector = if (regPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Slate400
                                    )
                                }
                            },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_password"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Team Selection
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Assign Team / Division", fontSize = 11.5.sp, color = Slate600, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                teams.take(3).forEach { team ->
                                    val isSelected = regTeamId == team.id
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            regTeamId = team.id
                                            regTeamName = team.name
                                        },
                                        label = { Text(team.name, fontSize = 11.sp) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null
                                    )
                                }
                            }
                        }

                        // Register Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                performRegistration()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_register_submit"),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Create Distributor Account",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
