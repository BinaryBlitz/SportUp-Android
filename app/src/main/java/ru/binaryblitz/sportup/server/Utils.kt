package ru.binaryblitz.sportup.server

import java.io.File

import okhttp3.MediaType
import okhttp3.RequestBody

object Utils {

    fun getImagePath(path: String): String {
        return ServerConfig.baseUrl + path
    }

    fun createRequestBodyForImage(path: String): RequestBody {
        val MEDIA_TYPE_PNG = MediaType.parse("image/png")
        val file = File(path)

        return RequestBody.create(MEDIA_TYPE_PNG, file)
    }
}
