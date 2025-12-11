package com.mervyn.ggcouriergo.repository

interface CloudinaryRepository {
    suspend fun uploadImage(byteArray: ByteArray): Result<String>
}
