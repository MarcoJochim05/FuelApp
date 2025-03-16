package com.example.fuelpricetracker.data

import com.example.fuelpricetracker.domain.models.FuelPricesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the Tankerkönig API
 * Documentation: https://creativecommons.tankerkoenig.de/
 */
interface FuelApiService {
    
    @GET("json/list.php")
    suspend fun getFuelStations(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("rad") radius: Int,
        @Query("type") type: String,
        @Query("sort") sort: String,
        @Query("apikey") apiKey: String
    ): Response<FuelPricesResponse>
    
    companion object {
        const val BASE_URL = "https://creativecommons.tankerkoenig.de/"
        
        // Note: In a real app, this would be stored securely, not hardcoded
        // For this example, you would need to replace this with a real API key
        const val API_KEY = "dcff3e27-d2ee-cb16-c67d-59b8ae726d17" // Demo API key
    }
}