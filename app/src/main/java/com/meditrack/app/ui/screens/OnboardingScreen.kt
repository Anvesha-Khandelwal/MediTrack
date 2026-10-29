package com.meditrack.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meditrack.app.ui.theme.*

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // Logo / title
        Text(
            text = "MediTrack",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TealPrimary
        )
        Text(
            text = "Medicine & Health Tracking",
            fontSize = 14.sp,
            color = MediumGray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Feature cards
        FeatureItem(
            icon = Icons.Filled.Medication,
            title = "Track Your Medicines",
            description = "Add your medicines with dosage, instructions, and reminder times."
        )
        Spacer(modifier = Modifier.height(20.dp))
        FeatureItem(
            icon = Icons.Filled.Notifications,
            title = "Never Miss a Dose",
            description = "Get timely reminders so you always take your medicines on schedule."
        )
        Spacer(modifier = Modifier.height(20.dp))
        FeatureItem(
            icon = Icons.Filled.TrendingUp,
            title = "Monitor Adherence",
            description = "View your dose history and adherence percentage at a glance."
        )
        Spacer(modifier = Modifier.height(20.dp))
        FeatureItem(
            icon = Icons.Filled.CalendarMonth,
            title = "Daily Schedule",
            description = "See today's medicine plan and mark doses as taken or skipped."
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
        ) {
            Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MediTrack does not provide medical advice.\nAlways consult your doctor.",
            fontSize = 11.sp,
            color = MediumGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFE0F2F1),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = OnWhite)
            Text(description, fontSize = 13.sp, color = MediumGray)
        }
    }
}
