package com.mervyn.ggcouriergo.ui.screens.driver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.ui.theme.CourierGoTheme
import okhttp3.*
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDeliverySummaryScreen(
    navController: NavController? = null,
    parcelId: String
) {
    val db = FirebaseFirestore.getInstance()
    val rtDb = FirebaseDatabase.getInstance().reference
    var parcelData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load parcel details
    DisposableEffect(parcelId) {
        val listener = db.collection("parcels")
            .document(parcelId)
            .addSnapshotListener { doc, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (doc != null && doc.exists()) {
                    parcelData = doc.data
                }
                isLoading = false
            }
        onDispose { listener.remove() }
    }

    // Launcher for camera/gallery
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) photoUri = uri
    }

    // Cloudinary upload function
    fun uploadToCloudinary(uri: Uri, onComplete: (String?) -> Unit) {
        uploading = true
        val cloudName = "YOUR_CLOUDINARY_CLOUD_NAME"
        val uploadPreset = "YOUR_UPLOAD_PRESET"

        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()

        if (bytes == null) {
            uploading = false
            onComplete(null)
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "photo.jpg",
                RequestBody.create(MediaType.parse("image/*"), bytes))
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                uploading = false
                onComplete(null)
            }

            override fun onResponse(call: Call, response: Response) {
                uploading = false
                if (response.isSuccessful) {
                    val json = JSONObject(response.body()?.string() ?: "")
                    val imageUrl = json.optString("secure_url", null)
                    onComplete(imageUrl)
                } else {
                    onComplete(null)
                }
            }
        })
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Delivery Summary") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Column
            }

            if (parcelData == null) {
                Text("Parcel not found.")
                return@Column
            }

            val data = parcelData!!

            Text("Parcel ID: ${data["id"]}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Pickup: ${data["pickupAddress"]}")
            Text("Dropoff: ${data["dropoffAddress"]}")
            Text("Receiver: ${data["receiverName"]} (${data["receiverPhone"]})")
            Text("Package: ${data["packageDetails"]}")
            Text("Status: ${data["status"]}")
            Spacer(Modifier.height(16.dp))

            Text("Upload Photo Proof", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            photoUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select Photo")
                }

                Button(
                    onClick = {
                        if (photoUri == null) return@Button
                        uploadToCloudinary(photoUri!!) { imageUrl ->
                            if (imageUrl != null) {
                                // Save URL in Realtime Database
                                rtDb.child("delivery_proofs").child(parcelId)
                                    .setValue(imageUrl)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uploading && photoUri != null
                ) {
                    if (uploading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Upload")
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDriverDeliverySummaryScreen() {
    val navController = rememberNavController()
    CourierGoTheme {
        DriverDeliverySummaryScreen(navController, parcelId = "123")
    }
}
