package com.example.ripple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ripple.ui.theme.RippleTheme

class Something : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Greeting()
        }
    }
}

@Composable
fun Greeting() {
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
    Text("Enter your username",
        style = TextStyle(
            fontSize = 20.sp
        ),
        modifier = Modifier
            .padding(horizontal = 20.dp))

    Spacer(modifier = Modifier .height(10.dp))

    Spacer(modifier = Modifier .height(10.dp))

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
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting()
    }
