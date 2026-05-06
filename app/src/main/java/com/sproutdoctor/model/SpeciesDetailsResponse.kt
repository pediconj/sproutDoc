package com.sproutdoctor.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeciesDetailsResponse(
    val id: Int,
    @SerialName("common_name")
    val commonName: String?,
    @SerialName("scientific_name")
    val scientificName: List<String>?,
    @SerialName("default_image")
    val defaultImage: DefaultImage? = null,
    @SerialName("watering_general_benchmark")
    val wateringGeneralBenchmark: WateringBenchmark? = null
)

@Serializable
data class DefaultImage(
    @SerialName("original_url")
    val originalUrl: String?,
    @SerialName("regular_url")
    val regularUrl: String?,
    @SerialName("medium_url")
    val mediumUrl: String?,
    @SerialName("small_url")
    val smallUrl: String?,
    @SerialName("thumbnail")
    val thumbnail: String?
)

@Serializable
data class WateringBenchmark(
    val value: String?,
    val unit: String?
)
