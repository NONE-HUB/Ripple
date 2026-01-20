package com.example.ripple

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.ripple.model.UserModel
import com.example.ripple.repository.UserRepoImpl
import com.google.firebase.auth.FirebaseAuth

/* ---------- Design Tokens ---------- */

private val GradientTop = Color(0xFFF3F6FB)
private val GradientBottom = Color.White
private val Surface = Color.White
private val PrimaryText = Color(0xFF111827)
private val SecondaryText = Color(0xFF6B7280)
private val BorderSubtle = Color(0xFFE5E7EB)
private val SkeletonBase = Color(0xFFE5E7EB)
private val SkeletonHighlight = Color(0xFFF3F4F6)

@Composable
fun ProfileScreen() {

    var user by remember { mutableStateOf<UserModel?>(null) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            UserRepoImpl().getUserById(userId) { success, _, fetchedUser ->
                if (success) user = fetchedUser
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(GradientTop, GradientBottom))
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /* ---------- Header ---------- */

        if (user == null) {
            SkeletonProfileHeader()
        } else {
            ProfileHeader(user!!)
        }

        Spacer(modifier = Modifier.height(32.dp))

        /* ---------- Content ---------- */

        if (user == null) {
            SkeletonCard()
        } else {
            ProfileCard(user!!)
        }
    }
}

/* -----------------------------------
   Header with Fade-in Image
----------------------------------- */

@Composable
private fun ProfileHeader(user: UserModel) {

    val painter = rememberAsyncImagePainter(
        model = user.photoUrl.ifEmpty { R.drawable.circle_regular_full }
    )

    val imageAlpha by animateFloatAsState(
        targetValue = if (painter.state is AsyncImagePainter.State.Success) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "imageFade"
    )

    Box(
        modifier = Modifier
            .size(124.dp)
            .clip(CircleShape)
            .background(Surface)
            .border(1.dp, BorderSubtle, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(116.dp)
                .clip(CircleShape)
                .alpha(imageAlpha)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = user.username,
        fontSize = 20.sp,
        color = PrimaryText
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Personal profile",
        fontSize = 14.sp,
        color = SecondaryText
    )
}

/* -----------------------------------
   Profile Card
----------------------------------- */

@Composable
private fun ProfileCard(user: UserModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            ProfileFieldItem("First name", user.firstName)
            ProfileFieldItem("Middle name", user.middleName)
            ProfileFieldItem("Last name", user.lastName)
            ProfileFieldItem("Username", user.username)
            ProfileFieldItem("Date of birth", user.dob)
        }
    }
}

/* -----------------------------------
   Skeletons (Shimmer)
----------------------------------- */

@Composable
private fun SkeletonProfileHeader() {
    ShimmerCircle(size = 124.dp)
    Spacer(modifier = Modifier.height(16.dp))
    ShimmerLine(width = 120.dp)
    Spacer(modifier = Modifier.height(8.dp))
    ShimmerLine(width = 90.dp)
}

@Composable
private fun SkeletonCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            repeat(5) {
                ShimmerLine()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/* -----------------------------------
   Shimmer Components
----------------------------------- */

@Composable
private fun ShimmerLine(width: Dp = Dp.Unspecified) {
    Box(
        modifier = Modifier
            .height(16.dp)
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .clip(RoundedCornerShape(8.dp))
            .background(shimmerBrush())
    )
}

@Composable
private fun ShimmerCircle(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush())
    )
}

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnim"
    )

    return Brush.linearGradient(
        colors = listOf(
            SkeletonBase,
            SkeletonHighlight,
            SkeletonBase
        ),
        start = androidx.compose.ui.geometry.Offset(translate - 200, 0f),
        end = androidx.compose.ui.geometry.Offset(translate, 0f)
    )
}

/* -----------------------------------
   Field
----------------------------------- */

@Composable
private fun ProfileFieldItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = label, fontSize = 13.sp, color = SecondaryText)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value.ifEmpty { "—" }, fontSize = 16.sp, color = PrimaryText)
    }
}

