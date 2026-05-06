package com.sproutdoctor.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scientificName: String,
    @DrawableRes val imageRes: Int,
    val imageUrl: String? = null,
    val lastWateredTimestamp: Long = System.currentTimeMillis(),
    val wateringIntervalDays: Float = 7f, // Default to 7 days
    val wateringBenchmark: String? = null
)
