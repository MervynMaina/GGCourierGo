package com.mervyn.ggcouriergo.ui.screens.driver

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.mervyn.ggcouriergo.data.DriverParcelDetailsViewModel
import com.mervyn.ggcouriergo.data.DriverParcelDetailsViewModelFactory
import com.mervyn.ggcouriergo.models.DriverParcelDetails
import com.mervyn.ggcouriergo.models.DriverParcelDetailsUIState
import com.mervyn.ggcouriergo.navigation.routeDeliverySummary
import com.mervyn.ggcouriergo.repository.DriverParcelRepository
import com.mervyn.ggcouriergo.ui.theme.GGCourierGoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverParcelDetailsScreen(
    navController: NavController,
    parcelId: String,
    viewModel: DriverParcelDetailsViewModel = viewModel(
        factory = DriverParcelDetailsViewModelFactory(DriverParcelRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(parcelId) {
        viewModel.loadParcel(parcelId)
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedPhotoUri = uri }
    )

    fun handleFinalDelivery(data: DriverParcelDetails) {
        if (selectedPhotoUri == null) {
            Toast.makeText(context, "Proof of delivery photo is required", Toast.LENGTH_SHORT).show()
            return
        }
        isUploading = true
        viewModel.finalizeDelivery(data.id, selectedPhotoUri!!, context) { success ->
            isUploading = false
            if (success) navController.navigate(routeDeliverySummary(data.id))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shipment Details", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                when (val state = uiState) {
                    is DriverParcelDetailsUIState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    is DriverParcelDetailsUIState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    is DriverParcelDetailsUIState.Success -> {
                        val data = state.parcel

                        // 1. Progress Info
                        StatusBadge(status = data.status)

                        Spacer(Modifier.height(16.dp))

                        // 2. Details Card
                        DeliveryDetailsCard(data)

                        Spacer(Modifier.height(24.dp))

                        // 3. Action Area
                        DeliveryActionButton(
                            parcel = data,
                            onStatusUpdate = { viewModel.updateStatus(data.id, it) },
                            onPhotoSelect = { photoLauncher.launch("image/*") },
                            onFinalizeDelivery = { handleFinalDelivery(data) },
                            selectedPhotoUri = selectedPhotoUri,
                            isLoading = isUploading
                        )
                    }
                }
            }

            if (isUploading) {
                Dialog(onDismissRequest = {}) {
                    Card(shape = RoundedCornerShape(16.dp)) {
                        Column(
                            Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(16.dp))
                            Text("Uploading Proof...", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp)))
            Spacer(Modifier.width(8.dp))
            Text(status.replace("_", " ").uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun DeliveryDetailsCard(data: DriverParcelDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SectionHeader(title = "Route Information", icon = Icons.Default.Map)
            DetailItem(label = "Pickup", value = data.pickupAddress, icon = Icons.Default.LocationOn)
            DetailItem(label = "Dropoff", value = data.dropoffAddress, icon = Icons.Default.Flag)

            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

            SectionHeader(title = "Customer Info", icon = Icons.Default.Person)
            DetailItem(label = "Receiver", value = data.receiverName, icon = Icons.Default.Badge)
            DetailItem(label = "Contact", value = data.receiverPhone, icon = Icons.Default.Phone)

            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

            SectionHeader(title = "Package", icon = Icons.Default.Inventory)
            Text(data.packageDetails, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DetailItem(label: String, value: String, icon: ImageVector) {
    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun DeliveryActionButton(
    parcel: DriverParcelDetails,
    onStatusUpdate: (String) -> Unit,
    onPhotoSelect: () -> Unit,
    onFinalizeDelivery: () -> Unit,
    selectedPhotoUri: Uri?,
    isLoading: Boolean
) {
    val commonModifier = Modifier.fillMaxWidth().height(56.dp)

    when (parcel.status.lowercase()) {
        "assigned" -> Button(onClick = { onStatusUpdate("picked_up") }, modifier = commonModifier, shape = RoundedCornerShape(16.dp)) {
            Text("CONFIRM PICKUP", fontWeight = FontWeight.Bold)
        }

        "picked_up" -> Button(onClick = { onStatusUpdate("in_transit") }, modifier = commonModifier, shape = RoundedCornerShape(16.dp)) {
            Text("START TRANSIT", fontWeight = FontWeight.Bold)
        }

        "in_transit" -> Column {
            Text("Proof of Delivery", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedPhotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedPhotoUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(onClick = onPhotoSelect, modifier = Modifier.align(Alignment.TopEnd).background(Color.White, RoundedCornerShape(50.dp))) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = onPhotoSelect, modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary.copy(0.1f), RoundedCornerShape(50.dp))) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Tap to take photo proof", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onFinalizeDelivery,
                enabled = selectedPhotoUri != null && !isLoading,
                modifier = commonModifier,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("MARK AS DELIVERED", fontWeight = FontWeight.Bold)
            }
        }
        "delivered" -> Surface(Modifier.fillMaxWidth(), color = Color(0xFFE8F5E9), shape = RoundedCornerShape(16.dp)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                Spacer(Modifier.width(12.dp))
                Text("Delivered successfully", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDriverParcelDetailsScreen() {
    val navController = rememberNavController()
    GGCourierGoTheme {
        val mockData = DriverParcelDetails(
            id = "GG-1001",
            pickupAddress = "123 Sender St",
            dropoffAddress = "456 Receiver Ave",
            receiverName = "John Doe",
            receiverPhone = "+1-555-1234",
            packageDetails = "Small Box",
            status = "in_transit",
            deliveredAt = null,
            deliveryPhotoUrl = null
        )
        Column(Modifier.padding(16.dp)) {
            DeliveryDetailsCard(mockData)
            Spacer(Modifier.height(20.dp))
            DeliveryActionButton(
                parcel = mockData,
                onStatusUpdate = {},
                onPhotoSelect = {},
                onFinalizeDelivery = {},
                selectedPhotoUri = null,
                isLoading = false
            )
        }
    }
}