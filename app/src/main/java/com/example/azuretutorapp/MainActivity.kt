package com.example.azuretutorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.azuretutorapp.ui.theme.AzureTutorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AzureTutorAppTheme {
                AzureTutorApp()
            }
        }
    }
}
