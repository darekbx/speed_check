package com.darekbx.speedcheck.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.speedcheck.di.appModule
import com.darekbx.speedcheck.di.modulesPreview
import com.darekbx.speedcheck.ui.theme.SpeedCheckTheme
import com.darekbx.speedcheck.utils.SpeedUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import java.text.SimpleDateFormat
import java.util.Locale

val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SpeedScreen(speedViewModel: SpeedViewModel = koinViewModel()) {
    var isRunning by remember { mutableStateOf(false) }
    val isLocationEnabled by remember { speedViewModel.isLocationEnabled() }
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (isRunning) {
            val locationFlow = speedViewModel.locationFlow.collectAsState(null)
            locationFlow.value?.let { location ->
                SpeedBox(location)
            } ?: run {
                Text(
                    text = "Waiting...",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(start = 2.dp, bottom = 14.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            StartButton(enabled = isLocationEnabled && locationPermissionState.allPermissionsGranted) {
                isRunning = true
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PermissionStatus(
                    "Permission granted",
                    locationPermissionState.allPermissionsGranted
                )
                PermissionStatus(
                    "Location is on",
                    isLocationEnabled
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun SpeedBox(location: Location) {
    val speedKmH = SpeedUtils.msToKm(location.speed)
    var maxSpeed by rememberSaveable { mutableFloatStateOf(0.0F) }
    val speedList = remember { mutableStateListOf<Float>() }

    LaunchedEffect(speedKmH) {
        if (speedKmH > maxSpeed) {
            maxSpeed = speedKmH
        }
        speedList.add(speedKmH)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        var centerBox by remember { mutableStateOf(Offset.Zero) }
        var boxSize by remember { mutableStateOf(IntSize.Zero) }
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned {
                    centerBox = it.positionInParent()
                    boxSize = it.size
                },
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = String.format("%.2f", speedKmH),
                fontSize = 84.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "km/h",
                fontSize = 28.sp,
                modifier = Modifier.padding(start = 2.dp, bottom = 14.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, centerBox.y.toInt() + boxSize.height)
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DetailsLabel("Max speed: ", "${String.format("%.2f", maxSpeed)} km/h")
            DetailsLabel(
                "Location: ",
                String.format("%.2f", location.latitude) +
                        ", ${String.format("%.2f", location.longitude)}"
            )
            DetailsLabel("Altitude: ", "${location.altitude.toInt()}m")
            DetailsLabel("Time: ", timeFormat.format(location.time))
        }

        if (speedList.size > 2) {
            ChartView(
                modifier = Modifier
                    .background(Color(0xFF202020))
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(120.dp)
                    .align(Alignment.BottomCenter),
                data = speedList,
                unit = "km/h"
            )
        }
    }
}

@Composable
private fun DetailsLabel(label: String, value: String) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = buildAnnotatedString {
            append(label)
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85F)
                )
            ) {
                append(value)
            }
        },
        fontSize = 20.sp,
        fontWeight = FontWeight.Light,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75F)
    )
}

@Composable
private fun PermissionStatus(label: String, isGranted: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isGranted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "",
                tint = Color.Green,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "",
                tint = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75F),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun StartButton(enabled: Boolean, onClick: () -> Unit = { }) {
    ElevatedButton(onClick, enabled = enabled) {
        Text("Start", modifier = Modifier.padding(16.dp), fontSize = 48.sp)
    }
}

@Preview
@Composable
fun SpeedBoxPreview() {
    SpeedCheckTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            SpeedBox(Location("").apply {
                this.speed = 12.4F
                this.altitude = 121.0
                this.latitude = 52.10
                this.longitude = 21.20
                this.time = 1735719410052L
            })
        }
    }
}

@Preview(device = Devices.PIXEL_6A)
@Composable
fun SpeedScreenPreview() {
    val context = LocalContext.current
    KoinApplication(application = {
        androidContext(context)
        modules(appModule + modulesPreview)
    }) {
        SpeedCheckTheme {
            SpeedScreen()
        }
    }
}
