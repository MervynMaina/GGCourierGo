package com.mervyn.ggcouriergo.ui.screens.dispatcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mervyn.ggcouriergo.data.NewParcelsViewModel
import com.mervyn.ggcouriergo.data.NewParcelsViewModelFactory
import com.mervyn.ggcouriergo.models.NewParcelsUIState
import com.mervyn.ggcouriergo.models.Parcel
import com.mervyn.ggcouriergo.repository.ParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme

@Composable
fun NewParcelsTab(
    onNavigateToAssignment: (String) -> Unit,
    viewModel: NewParcelsViewModel = viewModel(
        factory = NewParcelsViewModelFactory(ParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is NewParcelsUIState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        is NewParcelsUIState.Error -> {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is NewParcelsUIState.Success -> {
            if (state.parcels.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("Everything clear!", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("No new parcels waiting.", color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.parcels, key = { it.id }) { parcel ->
                        NewParcelCard(parcel = parcel) { onNavigateToAssignment(parcel.id) }
                    }
                }
            }
        }
    }
}

@Composable
fun NewParcelCard(parcel: Parcel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SENDER", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(parcel.senderName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black)
                }
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "PENDING",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("PICKUP", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(parcel.pickupAddress, maxLines = 1, color = Color.DarkGray, fontSize = 14.sp)
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp).size(16.dp),
                    tint = Color.LightGray
                )
                Column(Modifier.weight(1f)) {
                    Text("DROPOFF", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(parcel.dropoffAddress, maxLines = 1, color = Color.DarkGray, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ASSIGN DRIVER", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}
// Preview Note: Requires a mock UI State and Parcel object for a functional preview.
@Preview(showBackground = true)
@Composable
fun PreviewNewParcelsTab() {
    GGCourierGoTheme {
        // You would typically pass a function that does nothing for the preview
        NewParcelsTab(onNavigateToAssignment = {})
    }
}