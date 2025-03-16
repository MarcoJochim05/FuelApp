package com.example.fuelpricetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuelpricetracker.data.FuelRepository
import com.example.fuelpricetracker.domain.models.FuelStation
import com.example.fuelpricetracker.domain.models.FuelType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the fuel prices screen
 */
class FuelViewModel : ViewModel() {
    
    private val repository = FuelRepository()
    
    // UI state
    private val _uiState = MutableStateFlow(FuelUiState())
    val uiState: StateFlow<FuelUiState> = _uiState.asStateFlow()
    
    init {
        loadFuelPrices()
    }
    
    /**
     * Loads fuel prices from the repository
     */
    fun loadFuelPrices(
        fuelType: FuelType = _uiState.value.selectedFuelType,
        latitude: Double = _uiState.value.latitude,
        longitude: Double = _uiState.value.longitude,
        radius: Int = _uiState.value.searchRadius,
        sortByPrice: Boolean = _uiState.value.sortByPrice
    ) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            repository.getFuelStations(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                fuelType = fuelType
            )
                .onSuccess { stations ->
                    val sortedStations = if (sortByPrice) {
                        when (fuelType) {
                            FuelType.DIESEL -> stations.sortedBy { it.diesel ?: Double.MAX_VALUE }
                            FuelType.E10 -> stations.sortedBy { it.e10 ?: Double.MAX_VALUE }
                            FuelType.PREMIUM -> stations.sortedBy { it.premium ?: Double.MAX_VALUE }
                            FuelType.ALL -> stations.sortedBy { minOf(
                                it.diesel ?: Double.MAX_VALUE,
                                it.e10 ?: Double.MAX_VALUE,
                                it.premium ?: Double.MAX_VALUE
                            )}
                        }
                    } else {
                        stations
                    }
                    
                    _uiState.update { 
                        it.copy(
                            stations = sortedStations,
                            isLoading = false,
                            selectedFuelType = fuelType,
                            latitude = latitude,
                            longitude = longitude,
                            searchRadius = radius,
                            sortByPrice = sortByPrice
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            error = error.message ?: "Unknown error",
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    /**
     * Updates the selected fuel type and reloads the data
     */
    fun updateFuelType(fuelType: FuelType) {
        if (fuelType != _uiState.value.selectedFuelType) {
            loadFuelPrices(fuelType = fuelType)
        }
    }
    
    /**
     * Updates the location and reloads the data
     */
    fun updateLocation(latitude: Double, longitude: Double) {
        loadFuelPrices(latitude = latitude, longitude = longitude)
    }
    
    /**
     * Updates the search radius and reloads the data
     */
    fun updateSearchRadius(radius: Int) {
        loadFuelPrices(radius = radius)
    }
    
    /**
     * Updates the sort order and reloads the data
     */
    fun updateSortByPrice(sortByPrice: Boolean) {
        loadFuelPrices(sortByPrice = sortByPrice)
    }
    
    /**
     * Updates the location permission status
     */
    fun updateLocationPermission(hasPermission: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
    }
    
    /**
     * Updates the address and converts it to coordinates
     * For this example, we're using a simple approach without actual geocoding
     */
    fun updateAddress(address: String) {
        _uiState.update { it.copy(address = address) }
        
        // In a real app, this would use Android's Geocoder to convert the address to coordinates
        // For this example, we'll use some predefined locations based on simple string matching
        val coordinates = when {
            address.contains("berlin", ignoreCase = true) -> Pair(52.520008, 13.404954)
            address.contains("munich", ignoreCase = true) -> Pair(48.137154, 11.576124)
            address.contains("hamburg", ignoreCase = true) -> Pair(53.551086, 9.993682)
            address.contains("cologne", ignoreCase = true) -> Pair(50.937531, 6.960279)
            address.contains("frankfurt", ignoreCase = true) -> Pair(50.110922, 8.682127)
            else -> null
        }
        
        coordinates?.let { (lat, lng) ->
            loadFuelPrices(latitude = lat, longitude = lng)
        }
    }
}

/**
 * Data class representing the UI state
 */
data class FuelUiState(
    val stations: List<FuelStation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFuelType: FuelType = FuelType.ALL,
    val latitude: Double = 52.520008, // Default to Berlin coordinates
    val longitude: Double = 13.404954,
    val searchRadius: Int = 5,
    val sortByPrice: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val address: String = ""
)