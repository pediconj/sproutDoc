package com.sproutdoctor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sproutdoctor.data.AppDatabase
import com.sproutdoctor.network.APIService
import com.sproutdoctor.ui.components.HomeScreen
import com.sproutdoctor.ui.components.PlantDetailScreen
import com.sproutdoctor.ui.components.PlantSearchScreen
import com.sproutdoctor.ui.theme.SproutDoctorTheme
import com.sproutdoctor.viewmodel.PlantViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val plantDao = database.plantDao()
        val speciesDao = database.speciesDao()
        val apiService = APIService.create()

        setContent {
            SproutDoctorTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val viewModel: PlantViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return PlantViewModel(plantDao, speciesDao, apiService) as T
                        }
                    }
                )

                LaunchedEffect(Unit) {
                    viewModel.statusMessage.collect { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                val plants by viewModel.plants.collectAsState()
                val uiState by viewModel.uiState.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val searchResults by viewModel.searchResults.collectAsState()

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                plants = plants,
                                uiState = uiState,
                                onPlantClick = { plant ->
                                    navController.navigate("detail/${plant.id}")
                                },
                                onAddPlantClick = {
                                    navController.navigate("search")
                                }
                            )
                        }
                        composable("search") {
                            PlantSearchScreen(
                                query = searchQuery,
                                onQueryChange = viewModel::onSearchQueryChange,
                                searchResults = searchResults,
                                onSpeciesClick = { species ->
                                    viewModel.addPlantFromSpecies(species)
                                    navController.popBackStack()
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("detail/{plantId}") { backStackEntry ->
                            val plantId = backStackEntry.arguments?.getString("plantId")?.toIntOrNull()
                            val plant = plants.find { it.id == plantId }
                            if (plant != null) {
                                PlantDetailScreen(
                                    plant = plant,
                                    onNameChange = { newName ->
                                        viewModel.updatePlantName(plant.id, newName)
                                    },
                                    onWaterClick = {
                                        viewModel.waterPlant(plant)
                                    },
                                    onDeleteClick = {
                                        viewModel.deletePlant(plant)
                                        navController.popBackStack()
                                    },
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
