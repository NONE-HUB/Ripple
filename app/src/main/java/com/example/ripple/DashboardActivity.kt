package com.example.ripple

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.ripple.viewmodel.UserViewModel


import com.example.ripple.ui.theme.RippleTheme


class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {

    val context = LocalContext.current
    val activity = context as? Activity

    val email = activity?.intent?.getStringExtra("email") ?: ""
    val password = activity?.intent?.getStringExtra("password") ?: ""

    val navBarHeight = 110.dp
    val userViewModel: UserViewModel = viewModel()

    data class NavItem(val label: String, val icon: Int, val iconsize: Dp = 25.dp)

    var selectedItem by remember { mutableStateOf(1) }

    // Remove "Setting" tab
    val navList = listOf(
        NavItem("Home", R.drawable.home),
        NavItem("Create", R.drawable.create),
        NavItem("Profile", R.drawable.circle_regular_full),
        NavItem("Notification", R.drawable.notification)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = { Text("Ecommerce") },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.messagebox),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.messagebox),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.locker),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(navBarHeight),
                tonalElevation = 0.dp
            ) {
                navList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(top = 5.dp)
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = 9.sp,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier
                                    .padding(top = 0.dp, bottom = 5.dp)
                            )
                        },
                        onClick = { selectedItem = index },
                        selected = selectedItem == index
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedItem) {
                0 -> HomeScreen(userViewModel)
                1 -> CreateScreen()
                2 -> ProfileScreen()
                3 -> NotificationScreen()
                // No "Setting" tab anymore
                else -> HomeScreen(userViewModel)
            }
        }
    }
}





@Preview
@Composable
fun DashboardActivityPreview(){
    DashboardBody()
}