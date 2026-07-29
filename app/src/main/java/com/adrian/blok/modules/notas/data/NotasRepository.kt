package com.adrian.blok.modules.notas.data

import com.adrian.blok.core.network.ApiClient

class NotasRepository(
    private val api: NotasApi = ApiClient.create(),
) {
    suspend fun list(): List<NoteDto> = api.list()

    suspend fun create(title: String, content: String): NoteDto =
        api.create(NoteCreateRequest(title, content))

    suspend fun update(id: Int, title: String, content: String): NoteDto =
        api.update(id, NoteUpdateRequest(title = title, content = content))

    suspend fun delete(id: Int) = api.delete(id)
}
