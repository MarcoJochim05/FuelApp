package com.example.fuelpricetracker.domain.models

/**
 * Data class representing a fuel station with its prices
 */
data class FuelStation(
    val id: String,
    val name: String,
    val brand: String?,
    val street: String,
    val place: String,
    val lat: Double,
    val lng: Double,
    val dist: Double,
    val diesel: Double?,
    val e10: Double?,
    val premium: Double?,
    val isOpen: Boolean,
    val houseNumber: String?
)

/**
 * Enum representing different fuel types
 */
enum class FuelType {
    DIESEL,
    E10,
    PREMIUM,
    ALL;
    
    companion object {
        fun fromString(value: String): FuelType {
            return when (value.lowercase()) {
                "diesel" -> DIESEL
                "e10" -> E10
                "premium" -> PREMIUM
                else -> ALL
            }
        }
    }
}

/**
 * Data class representing the API response
 */
data class FuelPricesResponse(
    val ok: Boolean,
    val license: String,
    val data: String,
    val status: String,
    val stations: List<FuelStation>
)