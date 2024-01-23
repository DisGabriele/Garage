package com.example.garage.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val URL = "https://raw.githubusercontent.com/DisGabriele/Cars.json/main/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(URL)
    .build()


interface GarageApiService {
    @GET("cars.json")
    suspend fun getLogos() : List<Logo>

}
object GarageApi {
    val retrofitService: GarageApiService by lazy{
        retrofit.create(GarageApiService::class.java)
    }
}