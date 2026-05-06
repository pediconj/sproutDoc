package com.sproutdoctor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sproutdoctor.model.Plant
import com.sproutdoctor.network.UiState

@Composable
fun HomeScreen(
    plants: List<Plant>,
    uiState: UiState<Unit>,
    onPlantClick: (Plant) -> Unit,
    onAddPlantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlantClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Plant")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Error -> {
                    Text(
                        text = "Error: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    if (plants.isEmpty() && uiState is UiState.Success) {
                        Text("No plants yet. Add some!")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(plants) { plant ->
                                PlantItem(
                                    plant = plant,
                                    onClick = { onPlantClick(plant) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
