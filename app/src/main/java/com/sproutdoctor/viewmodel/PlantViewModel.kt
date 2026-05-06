package com.sproutdoctor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sproutdoctor.R
import com.sproutdoctor.data.PlantDao
import com.sproutdoctor.data.SpeciesDao
import com.sproutdoctor.model.Plant
import com.sproutdoctor.model.PlantSpecies
import com.sproutdoctor.network.APIService
import com.sproutdoctor.network.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlantViewModel(
    private val plantDao: PlantDao,
    private val speciesDao: SpeciesDao,
    private val apiService: APIService
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _statusMessage = MutableSharedFlow<String>()
    val statusMessage = _statusMessage.asSharedFlow()

    val plants: StateFlow<List<Plant>> = plantDao.getAllPlants()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: StateFlow<List<PlantSpecies>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else speciesDao.searchSpecies(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        checkAndFetchSpecies()
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun checkAndFetchSpecies() {
        viewModelScope.launch {
            if (speciesDao.getCount() == 0) {
                _uiState.value = UiState.Loading
                _statusMessage.emit("fetching data")
                try {
                    for (page in 1..13) {
                        val response = apiService.getSpeciesList(page = page, indoor = 1)
                        speciesDao.insertAll(response.data)
                    }
                    _uiState.value = UiState.Success(Unit)
                    _statusMessage.emit("fetched successfully")
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(e.message ?: "Unknown error")
                    _statusMessage.emit("error: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                _uiState.value = UiState.Success(Unit)
            }
        }
    }

    fun updatePlantName(plantId: Int, newName: String) {
        viewModelScope.launch {
            val plant = plantDao.getPlantById(plantId)
            if (plant != null) {
                plantDao.updatePlant(plant.copy(name = newName))
            }
        }
    }

    fun addPlantFromSpecies(species: PlantSpecies) {
        viewModelScope.launch {
            _statusMessage.emit("adding ${species.commonName ?: "plant"}...")
            var imageUrl: String? = null
            var wateringBenchmark: String? = null
            var wateringIntervalDays = 7f

            try {
                val details = apiService.getSpeciesDetails(species.id)
                imageUrl = details.defaultImage?.thumbnail
                wateringBenchmark = details.wateringGeneralBenchmark?.let {
                    "${it.value} ${it.unit}"
                }
                
                details.wateringGeneralBenchmark?.value?.let { valueStr ->
                    // Parse "7-10" or "7"
                    val parts = valueStr.split("-").mapNotNull { it.trim().toFloatOrNull() }
                    if (parts.isNotEmpty()) {
                        wateringIntervalDays = parts.average().toFloat()
                    }
                }
                _statusMessage.emit("added successfully")
            } catch (e: Exception) {
                _statusMessage.emit("error fetching details: ${e.message}")
                e.printStackTrace()
            }

            plantDao.insertPlant(
                Plant(
                    name = species.commonName ?: "Unknown",
                    scientificName = species.scientificName?.firstOrNull() ?: "Unknown",
                    imageRes = R.drawable.ic_launcher_background,
                    imageUrl = imageUrl,
                    lastWateredTimestamp = System.currentTimeMillis(),
                    wateringIntervalDays = wateringIntervalDays,
                    wateringBenchmark = wateringBenchmark
                )
            )
            _searchQuery.value = "" // Clear search
        }
    }

    fun waterPlant(plant: Plant) {
        viewModelScope.launch {
            plantDao.updatePlant(plant.copy(lastWateredTimestamp = System.currentTimeMillis()))
        }
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            plantDao.deletePlant(plant)
        }
    }

}
