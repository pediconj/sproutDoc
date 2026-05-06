package com.sproutdoctor.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "plant_species")
data class PlantSpecies(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @SerialName("common_name")
    val commonName: String?,
    @SerialName("scientific_name")
    val scientificName: List<String>? = null
)
