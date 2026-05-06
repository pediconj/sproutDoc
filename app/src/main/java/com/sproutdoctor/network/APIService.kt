package com.sproutdoctor.network

import com.sproutdoctor.BuildConfig
import com.sproutdoctor.model.SpeciesDetailsResponse
import com.sproutdoctor.model.SpeciesResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://perenual.com/api/v2/"
private val API_KEY = BuildConfig.PERENUAL_API_KEY

interface APIService {
    @GET("species-list")
    suspend fun getSpeciesList(
        @Query("key") apiKey: String = API_KEY,
        @Query("page") page: Int = 1,
        @Query("indoor") indoor: Int = 1
    ): SpeciesResponse

    @GET("species/details/{id}")
    suspend fun getSpeciesDetails(
        @Path("id") id: Int,
        @Query("key") apiKey: String = API_KEY
    ): SpeciesDetailsResponse

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        fun create(): APIService {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
                .create(APIService::class.java)
        }
    }
}
