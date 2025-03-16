package com.example.fuelpricetracker.data

import com.example.fuelpricetracker.domain.models.FuelPricesResponse
import com.example.fuelpricetracker.domain.models.FuelStation
import com.example.fuelpricetracker.domain.models.FuelType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository class that handles API calls to fetch fuel prices
 */
class FuelRepository {
    
    private val apiService: FuelApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        
        Retrofit.Builder()
            .baseUrl(FuelApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FuelApiService::class.java)
    }
    
    /**
     * Fetches fuel stations based on location and fuel type
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param radius Search radius in km
     * @param fuelType Type of fuel to filter by
     * @return List of fuel stations or null if the request failed
     */
    suspend fun getFuelStations(
        latitude: Double = 52.520008, // Default to Berlin coordinates
        longitude: Double = 13.404954,
        radius: Int = 5,
        fuelType: FuelType = FuelType.ALL
    ): Result<List<FuelStation>> = withContext(Dispatchers.IO) {
        try {
            val type = when (fuelType) {
                FuelType.DIESEL -> "diesel"
                FuelType.E10 -> "e10"
                FuelType.PREMIUM -> "super"
                FuelType.ALL -> "all"
            }
            
            val response = apiService.getFuelStations(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                type = type,
                sort = "dist",
                apiKey = FuelApiService.API_KEY
            )
            
            if (response.isSuccessful && response.body()?.ok == true) {
                val stations = response.body()?.stations ?: emptyList()
                
                // Filter stations based on fuel type if needed
                val filteredStations = when (fuelType) {
                    FuelType.DIESEL -> stations.filter { it.diesel != null }
                    FuelType.E10 -> stations.filter { it.e10 != null }
                    FuelType.PREMIUM -> stations.filter { it.premium != null }
                    FuelType.ALL -> stations
                }
                
                Result.success(filteredStations)
            } else {
                Result.failure(Exception("Failed to fetch fuel stations: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}