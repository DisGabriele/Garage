

package com.example.garage.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Logo (
    @Json(name = "name") var name: String,
    @Json(name = "img_url") var logo: String,
    @Json(name = "models") var models: List<String>
)