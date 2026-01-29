package com.example.ripple

// Firebase imports
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ripple.ui.theme.PurpleGrey80
import com.google.firebase.auth.FirebaseAuth

// Firebase imports

class LoginActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProfileActivity()
        }
    }
}



@Composable
fun ProfileActivity(){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as Activity

    var terms by remember { mutableStateOf(false) }

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    var localEmail by remember { mutableStateOf(sharedPreferences.getString("email", "") ?: "") }
    var localPassword by remember { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }

    var visibility by remember { mutableStateOf(false) }

    Scaffold() {padding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .background(Color.White)
        ){
            Spacer(modifier = Modifier .height(60.dp))
            Text("English(UK)",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier .height(60.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(105.dp)
                    .clip(CircleShape)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(R.drawable.instagram), contentDescription = null,
                    modifier = Modifier
                        .size(95.dp)
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop)
            }

            Spacer(modifier = Modifier .height(60.dp))

//            OutlinedTextField(
//                value = email,
//                onValueChange = { data ->
//                    email = data
//                },
//                leadingIcon = {Icon(painter = painterResource(R.drawable.messagebox), contentDescription = null,
//                    modifier = Modifier
//                        .size(25.dp))},
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text("Username or email")
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email
//                ),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Color.Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//            )

            OutlinedTextField(
                value = email,
                onValueChange = { data ->
                    email = data
                },
                leadingIcon = {Icon(painter = painterResource(R.drawable.messagebox), contentDescription = null,
                    modifier = Modifier
                        .size(25.dp))},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(15.dp),
                placeholder = {
                    Text("abc@gmail.com")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PurpleGrey80,
                    unfocusedContainerColor = PurpleGrey80,
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Transparent
                )

            )

            Spacer(modifier = Modifier .height(20.dp))

//            OutlinedTextField(
//                value = password,
//                onValueChange = {
//                    password = it
//                },
//                leadingIcon = {Icon(painter = painterResource(R.drawable.locker), contentDescription = null,
//                    modifier = Modifier
//                        .size(25.dp))},
//                trailingIcon = {
//                    IconButton(onClick = {
//                        visibility = !visibility
//                    }) {
//                        Icon(
//                            painter = if (visibility)
//                                painterResource(R.drawable.eye_open)
//                            else
//                                painterResource(R.drawable.eye_close),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .size(30.dp)
//                        )
//                    }
//                },
//                visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text("*********")
//                },
//
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Color.Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//
//            )

            OutlinedTextField(
                value = password,
                onValueChange = { data ->
                    password = data
                },
                leadingIcon = {Icon(painter = painterResource(R.drawable.locker), contentDescription = null,
                    modifier = Modifier
                        .size(25.dp))},
                trailingIcon = {
                    IconButton(onClick = {
                        visibility = !visibility
                    }) {
                        Icon(
                            painter = if (visibility)
                                painterResource(R.drawable.eye_open)
                            else
                                painterResource(R.drawable.eye_close),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(15.dp),
                placeholder = {
                    Text("*********")
                },

                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PurpleGrey80,
                    unfocusedContainerColor = PurpleGrey80,
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Transparent
                )

            )

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = terms,
                    onCheckedChange = {
                        terms = it
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Blue,
                        checkmarkColor = Color.White
                    )
                )
                Text("I agree to terms & conditions")
            }

//            Button(
//                onClick = {
//
//                    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
//                    val savedEmail = sharedPreferences.getString("email", "") ?: ""
//                    val savedUsername = sharedPreferences.getString("username", "") ?: ""
//                    val savedPassword = sharedPreferences.getString("password", "") ?: ""
//
//                    if (email.isEmpty() ||  password.isEmpty()) {
//                        Toast.makeText(context, "Please enter username/email and password", Toast.LENGTH_SHORT).show()
//                        return@Button
//                    }
//
//                    // Check if input matches either email or username, AND password matches
//                    val isValid = (email == savedEmail || email == savedUsername) && password == savedPassword
//
//                    if (isValid) {
//                        // Save current logged-in user
//                        sharedPreferences.edit().putString("currentUser", email).apply()
//
//                        val intent = Intent(context, DashboardActivity::class.java)
//                        context.startActivity(intent)
//                        activity.finish()
//                    } else {
//                        Toast.makeText(context, "Invalid login details", Toast.LENGTH_SHORT).show()
//                    }
//
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp)
//                    .height(60.dp),
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Text("Log In")
//            }

            Button(
                onClick = {
                    // Empty check
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please enter email and password",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // Login using Firebase
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(context, DashboardActivity::class.java)
                                context.startActivity(intent)
                                activity.finish()
                            } else {
                                Toast.makeText(
                                    context,
                                    task.exception?.message ?: "Login failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(60.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 15.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Log In")
            }



            Spacer(modifier = Modifier .height(17.dp))

            Text("Forgotten password?",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier .height(83.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(17.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(3.dp, Color.Blue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .clickable {
                                val intent = Intent(
                                    context,
                                    RegistrationActivity::class.java
                                )
                                context.startActivity(intent)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Create new Account",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.Blue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                }
            }

            Text("Ripple",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun ProfileActivityPreview(){
    ProfileActivity()
}

