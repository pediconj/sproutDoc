package com.sproutdoctor.data

import androidx.room.*
import com.sproutdoctor.model.PlantSpecies
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(species: List<PlantSpecies>)

    @Query("SELECT COUNT(*) FROM plant_species")
    suspend fun getCount(): Int

    @Query("SELECT * FROM plant_species WHERE commonName LIKE '%' || :query || '%'")
    fun searchSpecies(query: String): Flow<List<PlantSpecies>>
}
