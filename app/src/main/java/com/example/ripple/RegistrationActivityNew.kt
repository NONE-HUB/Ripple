package com.example.ripple

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ripple.ui.theme.PurpleGrey80
import com.example.ripple.ui.theme.RippleTheme

class RegistrationActivityNew : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistrationNewBody()
        }
    }
}

@Composable
fun RegistrationNewBody(){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }

    val activity = context as Activity

    val sharedPreference = context.getSharedPreferences(
        "User",
        Context.MODE_PRIVATE
    )

    val editor = sharedPreference.edit()

    Scaffold() {padding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
        ) {

            Spacer(modifier = Modifier .height(60.dp))

            Text("English(UK)",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()

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

            Text("Enter your email",
                style = TextStyle(
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp))

            Spacer(modifier = Modifier .height(10.dp))

            CharacterNew(
                value = email,
                onValueChange = { email = it },
                label = "Username",
                modifier = Modifier
            )

            Spacer(modifier = Modifier .height(20.dp))

            Text("Enter your password",
                style = TextStyle(
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp))

            Spacer(modifier = Modifier .height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
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
                modifier = Modifier.fillMaxWidth(),
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

            Button(
                onClick = {

                    // 1️⃣ Block empty fields
                    if (email.isEmpty() || password.isEmpty() ) {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // 2️⃣ Block if terms not accepted
                    if (!terms) {
                        Toast.makeText(
                            context,
                            "Please agree to terms & conditions",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // 3️⃣ Save to SharedPreferences
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.apply()

                    Toast.makeText(
                        context,
                        "Success",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 4️⃣ Go to LoginActivity
                    activity.finish()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(60.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 15.dp
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Sign Up")
            }





            Text(buildAnnotatedString {
                append("Already have account? ")

                withStyle(SpanStyle(color = Color.Blue)) {
                    append("Sign up")
                }
            }, modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp))

            Spacer(modifier = Modifier . height(210.dp))

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

@Composable
fun CharacterNew(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        shape = RoundedCornerShape(15.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = PurpleGrey80,
            unfocusedContainerColor = PurpleGrey80,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview
@Composable
fun RegistrationNewBodyPreview(){
    RegistrationNewBody()
}