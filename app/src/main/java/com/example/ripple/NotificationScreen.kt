package com.example.ripple

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.model.UserModel
import com.example.ripple.repository.UserRepoImpl
import com.example.ripple.viewmodel.ReportModel
import com.example.ripple.viewmodel.UserUiState
import com.example.ripple.viewmodel.UserViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun NotificationScreen(userViewModel: UserViewModel = viewModel()) {

    val uiState = userViewModel.uiState
    var showUserInfoDialog by remember { mutableStateOf(false) }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }




    val user = userViewModel.uiState // or fetched UserModel


    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        userViewModel.updatePhoto(uri)
    }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userRepo = remember { UserRepoImpl() }

    var showDialog by remember { mutableStateOf(false) }
    var showReports by remember { mutableStateOf(false) }
    var reportText by remember { mutableStateOf("") }
    var reportsList by remember { mutableStateOf(listOf<ReportModel>()) }
    var feedbackReportId by remember { mutableStateOf<String?>(null) }  // for feedback dialog
    var feedbackText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ===== Profile Card =====
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = uiState.photoUrl.takeIf { it.isNotEmpty() }
                                ?: R.drawable.circle_regular_full
                        ),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                    )

                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color.Gray.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Change Photo",
                            tint = Color.Black
                        )
                    }

                    if (uiState.photoUrl.isNotEmpty()) {
                        IconButton(
                            onClick = { userViewModel.updatePhoto(null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Photo",
                                tint = Color.Red
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = uiState.username.ifEmpty { "Username" },
                    fontSize = 22.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Expandable Sections =====
        ExpandableSection(title = "Settings & Privacy") {
            OptionItem("User Information") { showUserInfoDialog = true }
            OptionItem("Edit Profile") {
                showEditProfileDialog = true
            }
            OptionItem("Change Password") {
                showChangePasswordDialog = true
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExpandableSection(title = "Help & Support") {
            OptionItem("Report a Problem") {
                // Open the dialog when this option is clicked
                showDialog = true
            }
            OptionItem("Terms & Policies") {
                // Open Terms & Policies dialog
                showTermsDialog = true
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Action Buttons =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* Delete account */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4F)),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete Account", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }

            Button(
                onClick = { /* Logout */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout", color = Color.Black, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // ===== User Information Dialog =====
    val userModel = UserModel(
        userId = userViewModel.uiState.userId,
        firstName = userViewModel.uiState.firstName.orEmpty(),
        middleName = userViewModel.uiState.middleName.orEmpty(),
        lastName = userViewModel.uiState.lastName.orEmpty(),
        username = userViewModel.uiState.username.orEmpty(),
        email = userViewModel.uiState.email.orEmpty(),
        gender = userViewModel.uiState.gender.orEmpty(),
        dob = userViewModel.uiState.dob.orEmpty(),
        password = "",
        photoUrl = userViewModel.uiState.photoUrl.orEmpty()
    )

    if (showUserInfoDialog) {
        UserInformationDialog(
            user = userModel,
            onDismiss = { showUserInfoDialog = false }
        )
    }

//
//    if (showChangePasswordDialog) {
//        ChangePasswordDialog(
//            userViewModel = userViewModel,
//            onDismiss = { showChangePasswordDialog = false }
//        )
//    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            userViewModel = userViewModel,
            onDismiss = { showEditProfileDialog = false }
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Report a Problem") },
                text = {
                    OutlinedTextField(
                        value = reportText,
                        onValueChange = { reportText = it },
                        placeholder = { Text("Write your problem here...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (reportText.isNotBlank()) {
                            userRepo.reportProblem(userId, reportText) { success, msg ->
                                if (success) {
                                    Toast.makeText(context, "Report submitted", Toast.LENGTH_SHORT).show()
                                    reportText = ""
                                    showDialog = false
                                } else {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Enter a problem", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Submit")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Button(onClick = {
            // Fetch all reports
            userRepo.getAllReports { success, _, list ->
                if (success) {
                    reportsList = list
                    showReports = true
                } else {
                    Toast.makeText(context, "Failed to fetch reports", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("View All Reports")
        }
    }

    // ---------- Report Problem Dialog ----------
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Report a Problem") },
            text = {
                OutlinedTextField(
                    value = reportText,
                    onValueChange = { reportText = it },
                    placeholder = { Text("Write your problem here...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (reportText.isNotBlank()) {
                        userRepo.reportProblem(userId, reportText) { success, msg ->
                            if (success) {
                                Toast.makeText(context, "Report submitted", Toast.LENGTH_SHORT).show()
                                reportText = ""
                                showDialog = false
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Enter a problem", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    // ---------- Feedback Dialog ----------
    if (feedbackReportId != null) {
        AlertDialog(
            onDismissRequest = { feedbackReportId = null },
            title = { Text("Send Feedback") },
            text = {
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholder = { Text("Write feedback here...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val reportId = feedbackReportId!!
                    userRepo.sendFeedback(reportId, feedbackText) { success, msg ->
                        if (success) {
                            Toast.makeText(context, "Feedback sent", Toast.LENGTH_SHORT).show()
                            feedbackReportId = null
                            feedbackText = ""
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("Send") }
            },
            dismissButton = {
                Button(onClick = { feedbackReportId = null }) { Text("Cancel") }
            }
        )
    }

//    // ---------- View Reports Dialog ----------
//    if (showReports) {
//        AlertDialog(
//            onDismissRequest = { showReports = false },
//            title = { Text("All Reports") },
//            text = {
//                Column(
//                    modifier = Modifier
//                        .verticalScroll(rememberScrollState())
//                        .fillMaxWidth()
//                        .height(400.dp)
//                ) {
//                    reportsList.forEachIndexed { index, report ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 4.dp),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Column(modifier = Modifier.weight(1f)) {
//                                Text("User ID: ${report.userId}", fontWeight = FontWeight.Bold)
//                                Text("Message: ${report.message}")
//                            }
//
//                            Row {
//                                IconButton(onClick = {
//                                    feedbackReportId = report.id
//                                }) {
//                                    Icon(Icons.Default.Feedback, contentDescription = "Feedback")
//                                }
//
//                                IconButton(onClick = {
//                                    // Delete report
//                                    FirebaseDatabase.getInstance()
//                                        .getReference("Reports")
//                                        .child(report.id ?: "")
//                                        .removeValue()
//                                        .addOnCompleteListener { task ->
//                                            if (task.isSuccessful) {
//                                                Toast.makeText(context, "Report deleted", Toast.LENGTH_SHORT).show()
//                                                reportsList = reportsList.toMutableList().apply { removeAt(index) }
//                                            } else {
//                                                Toast.makeText(context, task.exception?.message ?: "Delete failed", Toast.LENGTH_SHORT).show()
//                                            }
//                                        }
//                                }) {
//                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
//                                }
//                            }
//                        }
//                        Divider()
//                    }
//                }
//            },
//            confirmButton = {
//                Button(onClick = { showReports = false }) { Text("Close") }
//            }
//        )
//    }

    // ---------- View Reports Dialog ----------
    if (showReports) {
        AlertDialog(
            onDismissRequest = { showReports = false },
            title = { Text("All Reports") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    itemsIndexed(reportsList) { index, report ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "User ID: ${report.userId}",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Message: ${report.message}")
                            }

                            Row {
                                IconButton(onClick = {
                                    feedbackReportId = report.id
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Feedback,
                                        contentDescription = "Feedback"
                                    )
                                }

                                IconButton(onClick = {
                                    FirebaseDatabase.getInstance()
                                        .getReference("Reports")
                                        .child(report.id ?: "")
                                        .removeValue()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Report deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                reportsList = reportsList
                                                    .toMutableList()
                                                    .apply { removeAt(index) }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    task.exception?.message ?: "Delete failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                        Divider()
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showReports = false }) {
                    Text("Close")
                }
            }
        )
    }


    // ---------- Terms & Policies Dialog ----------
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms & Policies") },
            text = {
                // Scrollable content if text is long
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                        1. **Introduction**  
                        Welcome to our app. By using our service, you agree to these Terms & Policies.
                        
                        2. **Account Requirements**  
                        You must be at least 13 years old to use this service. You are responsible for the confidentiality of your account.

                        3. **User Content**  
                        You retain ownership of the content you post. However, by posting, you grant us a license to use and display it.

                        4. **Prohibited Activities**  
                        Do not engage in harassment, spamming, or uploading harmful content.

                        5. **Privacy**  
                        We collect information to improve our service. Please refer to our Privacy Policy for details.

                        6. **Termination**  
                        We reserve the right to suspend or terminate accounts that violate these terms.

                        7. **Modifications**  
                        We may update these Terms & Policies periodically. Continued use of the service constitutes acceptance.

                        8. **Contact Us**  
                        For questions, contact our support team at support@example.com.
                    """.trimIndent()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }



}

@Composable
fun ExpandableSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    var expanded by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation),
                    tint = Color.Gray
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Column { content() }
            }
        }
    }
}

@Composable
fun OptionItem(optionName: String, onClick: () -> Unit) {
    Text(
        text = optionName,
        fontSize = 16.sp,
        color = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    )
}

@Composable
fun UserInformationDialog(user: UserModel, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column {
                ProfileField("First Name", user.firstName.ifEmpty { "—" })
                ProfileField("Middle Name", user.middleName.ifEmpty { "—" })
                ProfileField("Last Name", user.lastName.ifEmpty { "—" })
                ProfileField("Username", user.username.ifEmpty { "—" })
                ProfileField("Date of Birth", user.dob.ifEmpty { "—" })
                ProfileField("Email", user.email.ifEmpty { "—" })
                ProfileField("Gender", user.gender.ifEmpty { "—" })
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

//@Composable
//fun ChangePasswordDialog(
//    onDismiss: () -> Unit,
//    userViewModel: UserViewModel
//) {
//    var oldPassword by remember { mutableStateOf("") }
//    var newPassword by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//    var errorMessage by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(false) }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Change Password", fontWeight = FontWeight.Bold) },
//        text = {
//            Column(modifier = Modifier.fillMaxWidth()) {
//                OutlinedTextField(
//                    value = oldPassword,
//                    onValueChange = { oldPassword = it },
//                    label = { Text("Old Password") },
//                    singleLine = true,
//                    visualTransformation = PasswordVisualTransformation(),
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                OutlinedTextField(
//                    value = newPassword,
//                    onValueChange = { newPassword = it },
//                    label = { Text("New Password") },
//                    singleLine = true,
//                    visualTransformation = PasswordVisualTransformation(),
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                OutlinedTextField(
//                    value = confirmPassword,
//                    onValueChange = { confirmPassword = it },
//                    label = { Text("Confirm New Password") },
//                    singleLine = true,
//                    visualTransformation = PasswordVisualTransformation(),
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                if (errorMessage.isNotEmpty()) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(errorMessage, color = Color.Red, fontSize = 13.sp)
//                }
//
//                if (isLoading) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//                }
//            }
//        },
//        confirmButton = {
//            TextButton(onClick = {
//                errorMessage = ""
//                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
//                    errorMessage = "All fields are required"
//                    return@TextButton
//                }
//                if (newPassword != confirmPassword) {
//                    errorMessage = "New passwords do not match"
//                    return@TextButton
//                }
//
//                isLoading = true
//
//                // Re-authenticate with old password
//                val user = FirebaseAuth.getInstance().currentUser
//                val email = user?.email
//                if (user != null && email != null) {
//                    val credential = EmailAuthProvider.getCredential(email, oldPassword)
//                    user.reauthenticate(credential)
//                        .addOnSuccessListener {
//                            // Now update password
//                            user.updatePassword(newPassword)
//                                .addOnSuccessListener {
//                                    isLoading = false
//                                    onDismiss()
//                                }
//                                .addOnFailureListener { e ->
//                                    isLoading = false
//                                    errorMessage = "Failed to update password: ${e.message}"
//                                }
//                        }
//                        .addOnFailureListener { e ->
//                            isLoading = false
//                            errorMessage = "Old password is incorrect"
//                        }
//                } else {
//                    isLoading = false
//                    errorMessage = "User not logged in"
//                }
//
//            }) {
//                Text("Change")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        },
//        shape = RoundedCornerShape(20.dp)
//    )
//}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Visibility states for eye toggles
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Main dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ---------- Old Password ----------
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Old Password") },
                    singleLine = true,
                    visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { oldPasswordVisible = !oldPasswordVisible } // onClick explicitly
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (oldPasswordVisible) R.drawable.eye_open else R.drawable.eye_close
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ---------- New Password ----------
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { newPasswordVisible = !newPasswordVisible } // onClick explicitly
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (newPasswordVisible) R.drawable.eye_open else R.drawable.eye_close
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ---------- Confirm Password ----------
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible } // onClick explicitly
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (confirmPasswordVisible) R.drawable.eye_open else R.drawable.eye_close
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // ---------- Error message ----------
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = Color.Red, fontSize = 13.sp)
                }

                // ---------- Loading ----------
                if (isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                errorMessage = ""

                // ---------- Validation ----------
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    errorMessage = "All fields are required"
                    return@TextButton
                }
                if (newPassword != confirmPassword) {
                    errorMessage = "New passwords do not match"
                    return@TextButton
                }

                isLoading = true

                val user = FirebaseAuth.getInstance().currentUser
                val email = user?.email

                if (user != null && email != null) {
                    val credential = EmailAuthProvider.getCredential(email, oldPassword)

                    // Re-authenticate first
                    user.reauthenticate(credential)
                        .addOnSuccessListener {
                            // Update password
                            user.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    isLoading = false
                                    showSuccessDialog = true
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    errorMessage = "Failed to update password: ${e.message}"
                                }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            errorMessage = "Old password is incorrect"
                        }
                } else {
                    isLoading = false
                    errorMessage = "User not logged in"
                }

            }) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )

    // ---------- Success Dialog ----------
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onDismiss()
            },
            title = { Text("Success") },
            text = { Text("Password changed successfully") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}





@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 16.sp, color = Color.Black)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    userViewModel: UserViewModel,
    onClose: () -> Unit
) {
    val uiState = userViewModel.uiState
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        userViewModel.updatePhoto(uri)
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userRepo = remember { UserRepoImpl() }

    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ---------- Load User Data ----------
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userRepo.getUserById(userId) { success, _, user ->
                if (success && user != null) {
                    firstName = user.firstName
                    middleName = user.middleName.orEmpty()
                    lastName = user.lastName
                    username = user.username.orEmpty()
                    email = user.email
                    selectedDate = user.dob.orEmpty()
                    user.photoUrl?.let { userViewModel.updatePhoto(Uri.parse(it)) }
                }
            }
        }
    }

    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------- Header ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Edit Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Profile Photo ----------
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(140.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = uiState.photoUrl.ifEmpty { R.drawable.circle_regular_full }
                ),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            IconButton(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Change Photo")
            }

            if (uiState.photoUrl.isNotEmpty()) {
                IconButton(
                    onClick = { userViewModel.updatePhoto(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove Photo",
                        tint = Color.Red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- Input Fields ----------
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Middle Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = { datePicker.show() }) {
            Text(if (selectedDate.isEmpty()) "Select Date of Birth" else selectedDate)
        }

        errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        // ---------- Save Button ----------
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading,
            onClick = {
                errorMessage = null

                if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
                    errorMessage = "Please fill all required fields"
                    return@Button
                }

                scope.launch {
                    isLoading = true
                    try {
                        val updatedUser = UserModel(
                            userId = userId,
                            firstName = firstName,
                            middleName = middleName,
                            lastName = lastName,
                            username = username,
                            email = email,
                            dob = selectedDate,
                            photoUrl = uiState.photoUrl
                        )

                        userRepo.editProfile(userId, updatedUser) { success, message ->
                            if (success) {
                                Toast
                                    .makeText(context, "Profile updated", Toast.LENGTH_SHORT)
                                    .show()
                                onClose()
                            } else {
                                errorMessage = message
                            }
                        }
                    } finally {
                        isLoading = false
                    }
                }
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp))
            } else {
                Text("Save Changes")
            }
        }
    }
}


@Composable
fun EditProfileDialog(
    userViewModel: UserViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f), // almost full screen
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            EditProfileContent(
                userViewModel = userViewModel,
                onClose = onDismiss
            )
        }
    }
}




