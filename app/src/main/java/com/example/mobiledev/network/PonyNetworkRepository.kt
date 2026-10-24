package com.example.mobiledev.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PonyApiResponse(
    val status: Int,
    val data: List<RawPonyData>
)

@Serializable
data class RawPonyData(
    val id: Int,
    val name: String,
    val sex: String,
    val kind: List<String>,
    val image: List<String>
)

data class Pony(
    val id: Int,
    val name: String,
    val sex: String,
    val kind: String,
    val imageUrl: String
)

object PonyNetworkRepository {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getPonies(limit: Int = 5, offset: Int = 0): Result<List<Pony>> {
        return try {
            val response: PonyApiResponse = client.get("https://ponyapi.net/v1/character/by-residence/ponyville") {
                url {
                    parameters.append("limit", limit.toString())
                    parameters.append("offset", offset.toString())
                }
            }.body()

            if (response.status == 200) {
                val ponies = response.data.map { raw ->
                    Pony(
                        id = raw.id,
                        name = raw.name,
                        sex = raw.sex,
                        kind = raw.kind.firstOrNull() ?: "Неизвестно",
                        imageUrl = raw.image.firstOrNull() ?: ""
                    )
                }
                Result.success(ponies)
            } else {
                Result.failure(Exception("Ошибка сервера: код ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}