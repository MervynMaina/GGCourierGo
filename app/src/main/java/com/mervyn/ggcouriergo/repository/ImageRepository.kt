package com.mervyn.ggcouriergo.repository

import android.content.Context
import android.net.Uri
import com.mervyn.ggcouriergo.models.CloudinaryResponse
import com.mervyn.ggcouriergo.network.CloudinaryApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class ImageRepository(private val context: Context) {

    // Setup Retrofit with the Logging Interceptor you included in Gradle
    private val api: CloudinaryApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/v1_1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }

    suspend fun uploadProofOfDelivery(imageUri: Uri): String? {
        return try {
            val file = uriToFile(imageUri) ?: return null

            // Create MultipartBody.Part
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Create Upload Preset RequestBody (Must be "Unsigned" in Cloudinary Settings)
            val presetPart = "ggcouriergo" // <--- Change this!
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.uploadImage(filePart, presetPart)

            if (response.isSuccessful) {
                response.body()?.secure_url // Return the HTTPS URL
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper to convert URI to a temporary File
    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "upload_image.jpg")
            val outputStream = FileOutputStream(tempFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}