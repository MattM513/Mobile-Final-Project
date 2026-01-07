package fr.isep.subscout.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v4/latest/{base}")
    suspend fun getExchangeRates(@Path("base") base: String): ExchangeRateResponse
}

data class ExchangeRateResponse(
    @SerializedName("base") val base: String,
    @SerializedName("rates") val rates: Map<String, Double>
)
