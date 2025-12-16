package com.mervyn.ggcouriergo.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.google.gson.GsonBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object BackupUtils {
    fun exportDataToJson(context: Context, data: Any) {
        try {
            // 1. Setup Gson for pretty printing (so the file is readable)
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(data)

            // 2. Create a unique filename with a timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "GGCourier_Backup_$timestamp.json"

            // 3. Target the public Documents folder
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            // 4. Write the file
            file.writeText(jsonString)

            Toast.makeText(context, "Backup saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}