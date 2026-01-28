package com.example.ripple

import android.R.attr.visibility
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import UserModel
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color.Companion.Blue
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
import com.example.ripple.viewmodel.UserViewModel

import com.example.ripple.repository.UserRepoImpl
import com.example.ripple.ui.theme.RippleTheme
import com.example.ripple.ui.theme.PurpleGrey80
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistartionBody()
        }
    }
}

@Composable
fun RegistartionBody(){

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var userId by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }

    var firstname by remember { mutableStateOf("") }
    var middlename by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }



    val activity = context as Activity

    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDate by remember { mutableStateOf("") }
    val datepicker = DatePickerDialog(
        context,{
                _,year,month,day->
            selectedDate = "$year/${month+1}/$day"

        },year,month,day
    )

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

            Spacer(modifier = Modifier .height(10.dp))

//            Text("English(UK)",
//                style = TextStyle(
//                    textAlign = TextAlign.Center,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp,
//                    color = Color.Gray
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//
//            )

            Spacer(modifier = Modifier .height(10.dp))

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .size(105.dp)
//                    .clip(CircleShape)
//                    .padding(4.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Image(painter = painterResource(R.drawable.instagram), contentDescription = null,
//                    modifier = Modifier
//                        .size(95.dp)
//                        .clip(shape = CircleShape),
//                    contentScale = ContentScale.Crop)
//            }

            Spacer(modifier = Modifier .height(10.dp))

//            Text("Enter your username",
//                style = TextStyle(
//                    fontSize = 20.sp
//                ),
//                modifier = Modifier
//                    .padding(horizontal = 20.dp))
//
//            Spacer(modifier = Modifier .height(10.dp))

//            Character(
//                value = username,
//                onValueChange = { username = it },
//                label = "Username",
//                modifier = Modifier
//            )

//            OutlinedTextField(
//                value = userId,
//                onValueChange = { data ->
//                    userId= data
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text("userId")
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email
//                ),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//
//            )

            Spacer(modifier = Modifier .height(20.dp))

//            Text("Enter your Name",
//                style = TextStyle(
//                    fontSize = 20.sp
//                ),
//                modifier = Modifier
//                    .padding(horizontal = 20.dp))
//
//            Spacer(modifier = Modifier .height(10.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceAround
//            ) {
//
//                Text("First Name",
//                    style = TextStyle(
//                        fontSize = 10.sp
//                    ))
//
//                Text("Middle Name",
//                    style = TextStyle(
//                        fontSize = 10.sp
//                    ))
//
//                Text("Last Name",
//                    style = TextStyle(
//                        fontSize = 10.sp
//                    ))
//            }
//
//            Row() {
////                Character(
////                    value = firstname,
////                    onValueChange = { firstname = it },
////                    label = ".....",
////                    modifier = Modifier
////                        .weight(1f)
////                )
//
//            OutlinedTextField(
//                value = firstName,
//                onValueChange = { data ->
//                    firstName = data
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp)
//                    .weight(1f),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text(".....")
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email
//                ),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//
//            )
////
////                Character(
////                    value = middlename,
////                    onValueChange = { middlename = it },
////                    label = ".....",
////                    modifier = Modifier
////                        .weight(1f)
////                )
//
//            OutlinedTextField(
//                value = middleName,
//                onValueChange = { data ->
//                    middleName = data
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp)
//                    .weight(1f),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text(".....")
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email
//                ),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//
//            )
////
////                Character(
////                    value = lastname,
////                    onValueChange = { lastname = it },
////                    label = ".....",
////                    modifier = Modifier
////                        .weight(1f)
////                )
//
//            OutlinedTextField(
//                value = lastName,
//                onValueChange = { data ->
//                    lastName = data
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp)
//                    .weight(1f),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text(".....")
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email
//                ),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//
//            )
//            }
//
//            Spacer(modifier = Modifier .height(20.dp))
//
//            OutlinedTextField(
//                value = gender,
//                onValueChange = { data ->
//                    gender = data
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text("gender")
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Email
//                ),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//
//            )
//
//            Spacer(modifier = Modifier .height(20.dp))
//
//            Text("Enter your Date of Birth",
//                style = TextStyle(
//                    fontSize = 20.sp
//                ),
//                modifier = Modifier
//                    .padding(horizontal = 20.dp))
//
//            Spacer(modifier = Modifier .height(20.dp))
//
//            OutlinedTextField(
//                enabled = false,
//                value = selectedDate,
//                onValueChange = { data ->
//                    selectedDate = data
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        datepicker.show()
//                    }
//                    .padding(horizontal = 15.dp),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text("dd/mm/yyyy")
//                },
//
//                colors = TextFieldDefaults.colors(
//                    disabledIndicatorColor = Color.Transparent,
//                    disabledContainerColor = PurpleGrey80,
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//
//            )
//
//            Spacer(modifier = Modifier .height(20.dp))



            Spacer(modifier = Modifier .height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { data ->
                    email = data
                },
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
                    focusedIndicatorColor = Blue,
                    unfocusedIndicatorColor = Color.Transparent
                )

            )

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
                    focusedIndicatorColor = Blue,
                    unfocusedIndicatorColor = Color.Transparent
                )

            )

//            OutlinedTextField(
//                enabled = false,
//                value = selectedDate,
//                onValueChange = { data ->
//                    selectedDate = data
//                },
//                modifier = Modifier
//                    .fillMaxWidth().clickable{
//                        datepicker.show()
//                    }
//                    .padding(horizontal = 15.dp),
//                shape = RoundedCornerShape(15.dp),
//                placeholder = {
//                    Text("dd/mm/yyyy")
//                },
//
//                colors = TextFieldDefaults.colors(
//                    disabledIndicatorColor = Color.Transparent,
//                    disabledContainerColor = PurpleGrey80,
//                    focusedContainerColor = PurpleGrey80,
//                    unfocusedContainerColor = PurpleGrey80,
//                    focusedIndicatorColor = Color.Blue,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//            )

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
                    if (!terms) {
                        Toast.makeText(
                            context,
                            "please agree to terms &  conditions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        userViewModel.register(email,password){
                                success,message,userId->
                            if(success){
                                var model = UserModel(
                                    userId = userId,
                                    firstName = firstName,
                                    middleName = middleName,
                                    lastName = lastName,
                                    gender = gender,
                                    email = email,
                                    dob = selectedDate,
                                    password = password
                                )
                                userViewModel.addUserToDatabase(
                                    userId,model
                                ){
                                        success,message->
                                    if(success){
                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Navigate to LoginActivity
                                        val intent = Intent(context, LoginActivity::class.java)
                                        context.startActivity(intent)
                                        activity.finish() // Close RegistrationActivity

                                    }else{
                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }else{
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

//                        editor.putString("email",email)
//                        editor.putString("password",password)
//                        editor.putString("date",selectedDate)
//
//                        editor.apply()
//                        Toast.makeText(
//                            context,
//                            "Success",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        activity.finish()

                    }
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

//            Button(
//                onClick = {
//                    if (!terms) {
//                        Toast.makeText(
//                            context,
//                            "please agree to terms & conditions",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        return@Button
//                    }
//
//                    // STEP 1: Anonymous authentication
//                    FirebaseAuth.getInstance()
//                        .signInAnonymously()
//                        .addOnSuccessListener {
//
//                            val userId = it.user!!.uid
//
//                            val model = UserModel(
//                                userId = userId,
//                                firstName = firstName,
//                                middleName = middleName,
//                                lastName = lastName,
//                                gender = gender,
//                                dob = selectedDate
//                            )
//
//                            // STEP 2: Save profile data
//                            FirebaseDatabase.getInstance().reference
//                                .child("users")
//                                .child(userId)
//                                .setValue(model)
//                                .addOnSuccessListener {
//
//                                    // STEP 3: Go to email/password screen
//                                    val intent = Intent(
//                                        context,
//                                        RegistrationActivityNew::class.java
//                                    )
//                                    context.startActivity(intent)
//                                    activity.finish()
//                                }
//                                .addOnFailureListener {
//                                    Toast.makeText(
//                                        context,
//                                        it.message,
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(
//                                context,
//                                it.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 15.dp)
//                    .height(60.dp),
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 15.dp
//                ),
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Text("Continue")
//            }





            Spacer(modifier = Modifier .height(85.dp))

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
fun Character(
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
fun RegistrationBodyPreview(){
    RegistartionBody()
}