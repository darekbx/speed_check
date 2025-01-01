package com.darekbx.speedcheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.darekbx.speedcheck.ui.SpeedScreen
import com.darekbx.speedcheck.ui.theme.SpeedCheckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedCheckTheme {
                SpeedScreen()
            }
        }
    }
}
