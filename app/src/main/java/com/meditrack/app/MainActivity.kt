package com.meditrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.meditrack.app.ui.MediTrackNavHost
import com.meditrack.app.ui.theme.MediTrackTheme

/**
 * Single Activity hosting all Compose screens via Navigation.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediTrackTheme {
                MediTrackNavHost()
            }
        }
    }
}
