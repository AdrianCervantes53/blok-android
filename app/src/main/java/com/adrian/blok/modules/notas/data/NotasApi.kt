package com.adrian.blok.modules.notas.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class NoteDto(
    val id: Int,
    val title: String,
    val content: String,
    val created_at: String,
    val updated_at: String,
)

data class NoteCreateRequest(
    val title: String,
    val content: String,
)

data class NoteUpdateRequest(
    val title: String? = null,
    val content: String? = null,
)

interface NotasApi {
    @GET("notas")
    suspend fun list(): List<NoteDto>

    @POST("notas")
    suspend fun create(@Body body: NoteCreateRequest): NoteDto

    @PATCH("notas/{id}")
    suspend fun update(@Path("id") id: Int, @Body body: NoteUpdateRequest): NoteDto

    @DELETE("notas/{id}")
    suspend fun delete(@Path("id") id: Int)
}
