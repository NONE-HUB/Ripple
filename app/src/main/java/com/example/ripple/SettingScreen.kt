package com.example.ripple

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar


@Composable
fun SettingScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val currentUserKey = sharedPreferences.getString("currentUser", "") ?: ""
    val editor = sharedPreferences.edit()

    // Load current info from SharedPreferences
    var firstname by remember { mutableStateOf(sharedPreferences.getString("firstname", "") ?: "") }
    var middlename by remember { mutableStateOf(sharedPreferences.getString("middlename", "") ?: "") }
    var lastname by remember { mutableStateOf(sharedPreferences.getString("lastname", "") ?: "") }
    var username by remember { mutableStateOf(sharedPreferences.getString("username", "") ?: "") }
    var email by remember { mutableStateOf(sharedPreferences.getString("email", "") ?: "") }
    var password by remember { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }
    var selectedDate by remember { mutableStateOf(sharedPreferences.getString("selectedDate", "") ?: "") }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = "$year/${month + 1}/$day"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Edit User Info", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstname,
                onValueChange = { firstname = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = middlename,
                onValueChange = { middlename = it },
                label = { Text("Middle Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { datePicker.show() }) {
                Text(text = if (selectedDate.isEmpty()) "Select Date" else selectedDate)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Validate required fields
                    if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Save updated info
                    editor.putString("firstname", firstname)
                    editor.putString("middlename", middlename)
                    editor.putString("lastname", lastname)
                    editor.putString("username", username)
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.putString("selectedDate", selectedDate)
                    editor.apply()

                    Toast.makeText(context, "Information updated successfully", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}

