package com.adrian.blok.modules.notas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adrian.blok.modules.notas.data.NoteDto
import com.adrian.blok.modules.notas.data.NotasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class NotasUiState(
    val loading: Boolean = true,
    val notes: List<NoteDto> = emptyList(),
    val error: String? = null,
    val busy: Boolean = false,
)

class NotasViewModel(
    private val repository: NotasRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(NotasUiState())
    val state: StateFlow<NotasUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                _state.value = NotasUiState(loading = false, notes = repository.list())
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "No se pudieron cargar las notas",
                )
            }
        }
    }

    fun create(title: String, content: String, onDone: () -> Unit) {
        mutate(onDone) { repository.create(title, content) }
    }

    fun update(id: Int, title: String, content: String, onDone: () -> Unit) {
        mutate(onDone) { repository.update(id, title, content) }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(busy = true, error = null)
            try {
                repository.delete(id)
                _state.value = _state.value.copy(
                    busy = false,
                    notes = _state.value.notes.filterNot { it.id == id },
                )
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    busy = false,
                    error = e.message() ?: "No se pudo borrar",
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    busy = false,
                    error = e.message ?: "No se pudo borrar",
                )
            }
        }
    }

    private fun mutate(onDone: () -> Unit, block: suspend () -> NoteDto) {
        viewModelScope.launch {
            _state.value = _state.value.copy(busy = true, error = null)
            try {
                block()
                val notes = repository.list()
                _state.value = NotasUiState(loading = false, notes = notes, busy = false)
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    busy = false,
                    error = e.message ?: "No se pudo guardar",
                )
            }
        }
    }

    companion object {
        fun factory(repository: NotasRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotasViewModel(repository) as T
                }
            }
    }
}
