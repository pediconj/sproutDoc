package com.sproutdoctor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sproutdoctor.model.PlantSpecies

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantSearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<PlantSpecies>,
    onSpeciesClick: (PlantSpecies) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Plants") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Search by common name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults) { species ->
                    ListItem(
                        headlineContent = { Text(species.commonName ?: "Unknown") },
                        supportingContent = { Text(species.scientificName?.joinToString(", ") ?: "Unknown") },
                        modifier = Modifier.clickable { onSpeciesClick(species) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
