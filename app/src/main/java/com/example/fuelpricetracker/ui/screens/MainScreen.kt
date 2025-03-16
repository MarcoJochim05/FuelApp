package com.example.fuelpricetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fuelpricetracker.R
import com.example.fuelpricetracker.domain.models.FuelStation
import com.example.fuelpricetracker.domain.models.FuelType
import com.example.fuelpricetracker.ui.FuelUiState
import androidx.compose.material.icons.filled.Search
import com.example.fuelpricetracker.ui.FuelViewModel

/**
 * Main screen showing fuel prices
 */
@Composable
fun MainScreen(viewModel: FuelViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        LocationSelector(
            latitude = uiState.latitude,
            longitude = uiState.longitude,
            searchRadius = uiState.searchRadius,
            sortByPrice = uiState.sortByPrice,
            hasLocationPermission = uiState.hasLocationPermission,
            onLocationUpdate = viewModel::updateLocation,
            onRadiusUpdate = viewModel::updateSearchRadius,
            onSortByPriceUpdate = viewModel::updateSortByPrice,
            onPermissionUpdate = viewModel::updateLocationPermission,
            address = uiState.address,
            onAddressUpdate = viewModel::updateAddress
        )
        
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error!!, onRetry = { viewModel.loadFuelPrices() })
            uiState.stations.isEmpty() -> EmptyState()
            else -> StationsList(uiState.stations, uiState.selectedFuelType)
        }
    }
}

/**
 * Location selector component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelector(
    latitude: Double,
    longitude: Double,
    searchRadius: Int,
    sortByPrice: Boolean,
    hasLocationPermission: Boolean,
    onLocationUpdate: (Double, Double) -> Unit,
    onRadiusUpdate: (Int) -> Unit,
    onSortByPriceUpdate: (Boolean) -> Unit,
    onPermissionUpdate: (Boolean) -> Unit,
    address: String = "",
    onAddressUpdate: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.location_selector),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Address input field
            Column {
                Text(
                    text = "Enter Address",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = onAddressUpdate,
                        placeholder = { Text("e.g. Berlin, Munich, Hamburg") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            // Search with the current address
                            if (address.isNotEmpty()) {
                                onAddressUpdate(address)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = "Search")
                    }
                }
                
                Text(
                    text = "Try: Berlin, Munich, Hamburg, Cologne, Frankfurt",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search radius slider
            Text(
                text = stringResource(R.string.search_radius),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = stringResource(R.string.radius_km, searchRadius.toString()),
                style = MaterialTheme.typography.bodySmall
            )
            
            Slider(
                value = searchRadius.toFloat(),
                onValueChange = { onRadiusUpdate(it.toInt()) },
                valueRange = 1f..25f,
                steps = 24,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sort by price switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sort_by_price),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                Switch(
                    checked = sortByPrice,
                    onCheckedChange = onSortByPriceUpdate
                )
            }
        }
    }
}

/**
 * List of fuel stations
 */
@Composable
fun StationsList(stations: List<FuelStation>, selectedFuelType: FuelType) {
    LazyColumn {
        items(stations) { station ->
            StationItem(station, selectedFuelType)
        }
    }
}

/**
 * Individual fuel station item
 */
@Composable
fun StationItem(station: FuelStation, selectedFuelType: FuelType) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.LocalGasStation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${station.street} ${station.houseNumber ?: ""}, ${station.place}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = stringResource(R.string.distance, String.format("%.1f", station.dist)),
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            
            // Display fuel prices
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                station.diesel?.let {
                    PriceItem(title = stringResource(R.string.diesel), price = it, 
                        isHighlighted = selectedFuelType == FuelType.DIESEL || selectedFuelType == FuelType.ALL)
                }
                
                station.e10?.let {
                    PriceItem(title = stringResource(R.string.e10), price = it,
                        isHighlighted = selectedFuelType == FuelType.E10 || selectedFuelType == FuelType.ALL)
                }
                
                station.premium?.let {
                    PriceItem(title = stringResource(R.string.supere10), price = it,
                        isHighlighted = selectedFuelType == FuelType.PREMIUM || selectedFuelType == FuelType.ALL)
                }
            }
        }
    }
}

/**
 * Individual price item
 */
@Composable
fun PriceItem(title: String, price: Double, isHighlighted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(R.string.price, String.format("%.3f", price)),
            style = MaterialTheme.typography.titleMedium,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Loading state
 */
@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.loading))
        }
    }
}

/**
 * Error state
 */
@Composable
fun ErrorState(errorMessage: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.error_loading))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

/**
 * Empty state
 */
@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.no_stations))
    }
}