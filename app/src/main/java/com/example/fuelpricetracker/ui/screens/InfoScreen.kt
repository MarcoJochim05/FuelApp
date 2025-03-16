package com.example.fuelpricetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fuelpricetracker.R
import com.example.fuelpricetracker.ui.theme.DieselColor
import com.example.fuelpricetracker.ui.theme.E10Color
import com.example.fuelpricetracker.ui.theme.PremiumColor

/**
 * Information screen showing details about fuel types, pricing, and API
 */
@Composable
fun InfoScreen() {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(R.string.info_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Fuel Types Section
        InfoSection(
            title = stringResource(R.string.info_fuel_types),
            content = {
                FuelTypeInfo(
                    title = stringResource(R.string.diesel),
                    description = stringResource(R.string.info_diesel),
                    color = DieselColor
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FuelTypeInfo(
                    title = stringResource(R.string.e10),
                    description = stringResource(R.string.info_e10),
                    color = E10Color
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FuelTypeInfo(
                    title = stringResource(R.string.supere10),
                    description = stringResource(R.string.info_super),
                    color = PremiumColor
                )
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Pricing Section
        InfoSection(
            title = stringResource(R.string.info_pricing),
            content = {
                Text(text = stringResource(R.string.info_pricing_text))
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // API Information Section
        InfoSection(
            title = stringResource(R.string.info_api),
            content = {
                Text(text = stringResource(R.string.info_api_text))
            }
        )
    }
}

/**
 * Reusable section component for the info screen
 */
@Composable
fun InfoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

/**
 * Component for displaying fuel type information
 */
@Composable
fun FuelTypeInfo(
    title: String,
    description: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.size(16.dp),
            color = color,
            shape = MaterialTheme.shapes.small
        ) {}
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}