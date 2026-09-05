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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: CrmViewModel,
    modifier: Modifier = Modifier
) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val members by viewModel.members.collectAsState()
    val focusManager = LocalFocusManager.current

    fun performLogin() {
        if (userId.isBlank()) {
            errorMessage = "Please enter your User ID or Distributor ID"
            return
        }
        if (password.isBlank()) {
            errorMessage = "Please enter your password"
            return
        }
        isSubmitting = true
        errorMessage = null
        val (success, err) = viewModel.login(userId, password)
        isSubmitting = false
        if (!success) {
            errorMessage = err ?: "Invalid credentials. Please try again."
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
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
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_smart_group_logo),
                    contentDescription = "Smart Group Logo",
                    modifier = Modifier.size(68.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                text = "Enterprise Management & CRM Portal",
                fontSize = 11.sp,
                color = Slate400,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sign In to App",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Admin or Individual Distributor Login",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Indigo50)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SECURE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Indigo600
                            )
                        }
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

                    // User ID Field
                    OutlinedTextField(
                        value = userId,
                        onValueChange = {
                            userId = it
                            errorMessage = null
                        },
                        label = { Text("User ID / Username") },
                        placeholder = { Text("admin or distributor ID") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Indigo600)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_user_id"),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
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
                        placeholder = { Text("Enter password") },
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

                    // Login Button
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

                    // Session tracking notification note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Individual last login and current login timestamps will be recorded.",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security & Privacy Card (Confidential Distributor Logins)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Indigo900),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Indigo300,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Confidential Distributor Security",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Data isolation & role-based credentials",
                                fontSize = 10.sp,
                                color = Slate400
                            )
                        }
                    }

                    // Strict Security Notice
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate900.copy(alpha = 0.6f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Amber400,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Private Distributor Logins",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Amber300
                                )
                            }
                            Text(
                                text = "Each team member's login credentials and data are strictly private. No other team member can view or access your login details. Only the Super Admin has full administrative visibility across all accounts.",
                                fontSize = 11.sp,
                                color = Slate300,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Admin Quick Access Button (for administrative evaluation)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate700)
                            .clickable {
                                userId = "admin"
                                password = "admin123"
                                errorMessage = null
                            }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Amber500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Super Admin (All-Over Access)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "User ID: admin  •  Pass: admin123",
                                    fontSize = 11.sp,
                                    color = Amber300
                                )
                            }
                        }
                        Text("Fill Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Amber400)
                    }

                    Text(
                        text = "Distributors: Enter your private User ID and Password issued by Super Admin.",
                        fontSize = 10.sp,
                        color = Slate400,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
