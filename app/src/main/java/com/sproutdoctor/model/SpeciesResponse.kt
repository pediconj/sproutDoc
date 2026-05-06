package com.sproutdoctor.model

import kotlinx.serialization.Serializable

@Serializable
data class SpeciesResponse(
    val data: List<PlantSpecies>
)
