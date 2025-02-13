package com.example.pocketpilotjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pocketpilotjetpackcompose.ui.theme.PocketPilotJetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketPilotJetpackComposeTheme {
                PocketPilotApp()
            }
        }
    }
}

// Data class for Stop
data class Stop(
    val name: String,
    var reached: Boolean,
    val visaRequirements: String,
    val remainingDistance: Double,
    val remainingTime: Double
)

@Composable
fun PocketPilotApp() {
    var isKm by remember { mutableStateOf(true) }
    var stops by remember {
        mutableStateOf(
            listOf(
                Stop("Mumbai", false, "Visa not required", 100.0, 1.0),
                Stop("Jaipur", false, "Visa not required", 200.0, 1.5),
                Stop("Delhi", false, "Visa not required", 200.0, 2.0),
                Stop("London", false, "Visa required", 1500.0, 12.0),
                Stop("Paris", false, "Visa required", 150.0, 2.0)
            )
        )
    }
    var currentStopIndex by remember { mutableStateOf(0) }

    val totalCovered = stops.filter { it.reached }.sumOf { it.remainingDistance }
    val totalRemaining = stops.filter { !it.reached }.sumOf { it.remainingDistance }
    val progress = (stops.count { it.reached }.toFloat() / stops.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "PocketPilot Logo",
            modifier = Modifier
                .size(150.dp) // Adjust size as needed
                .align(Alignment.CenterHorizontally)
        )
        
//        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "STOPS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        // Scrollable List of Stops
        LazyColumn(
            modifier = Modifier
                .weight(1f) // This makes the list take all available space except bottom UI
                .fillMaxWidth()
        ) {
            items(stops) { stop ->
                StopCard(stop, isKm)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom UI: Distance Info and Toggle
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Distance Covered: ${formatDistance(totalCovered, isKm)}")
            Text("Total Distance Remaining: ${formatDistance(totalRemaining, isKm)}")

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle Button for Unit Change
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Distance in Miles  ")
                Switch(
                    checked = !isKm,
                    onCheckedChange = { isKm = !isKm }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Next Stop Button
            Button(
                onClick = {
                    if (currentStopIndex < stops.size) {
                        stops = stops.toMutableList().apply {
                            this[currentStopIndex] = this[currentStopIndex].copy(
                                reached = true,
                                remainingDistance = 0.0,
                                remainingTime = 0.0
                            )
                        }
                        currentStopIndex++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next Stop")
            }
        }
    }
}

// Composable for Each Stop Card
@Composable
fun StopCard(stop: Stop, isKm: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (stop.reached) Color.Green else Color.Red
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stop.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Reached: ${if (stop.reached) "Yes" else "No"}")
            Text("Visa Requirements: ${stop.visaRequirements}")
            Text("Remaining Distance: ${formatDistance(stop.remainingDistance, isKm)}")
            Text("Remaining Time: ${stop.remainingTime} hours")
        }
    }
}

// Function to Format Distance
fun formatDistance(distance: Double, isKm: Boolean): String {
    return if (isKm) {
        "$distance km"
    } else {
        "${distance * 0.62137} miles"
    }
}
