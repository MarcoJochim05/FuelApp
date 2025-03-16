package com.example.fuelpricetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fuelpricetracker.ui.screens.FuelAppNavigation
import com.example.fuelpricetracker.ui.theme.FuelPriceTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FuelPriceTrackerTheme {
                FuelAppNavigation()
            }
        }
    }
}