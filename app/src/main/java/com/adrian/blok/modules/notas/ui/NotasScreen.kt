package com.adrian.blok.modules.notas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.adrian.blok.modules.notas.data.NoteDto
import com.adrian.blok.modules.notas.viewmodel.NotasUiState

private sealed interface NotasMode {
    data object List : NotasMode
    data object Create : NotasMode
    data class Edit(val note: NoteDto) : NotasMode
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasScreen(
    userEmail: String,
    state: NotasUiState,
    onLogout: () -> Unit,
    onCreate: (String, String, () -> Unit) -> Unit,
    onUpdate: (Int, String, String, () -> Unit) -> Unit,
    onDelete: (Int) -> Unit,
) {
    var mode by remember { mutableStateOf<NotasMode>(NotasMode.List) }
    var pendingDelete by remember { mutableStateOf<NoteDto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Blok", fontWeight = FontWeight.Bold)
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Salir")
                    }
                },
            )
        },
        floatingActionButton = {
            if (mode is NotasMode.List) {
                FloatingActionButton(onClick = { mode = NotasMode.Create }) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva nota")
                }
            }
        },
    ) { padding ->
        when (val current = mode) {
            NotasMode.List -> NotasList(
                padding = padding,
                state = state,
                onEdit = { mode = NotasMode.Edit(it) },
                onDeleteRequest = { pendingDelete = it },
            )

            NotasMode.Create -> NoteEditor(
                padding = padding,
                titleLabel = "Nueva nota",
                initialTitle = "",
                initialContent = "",
                busy = state.busy,
                error = state.error,
                onCancel = { mode = NotasMode.List },
                onSave = { title, content ->
                    onCreate(title, content) { mode = NotasMode.List }
                },
            )

            is NotasMode.Edit -> NoteEditor(
                padding = padding,
                titleLabel = "Editar nota",
                initialTitle = current.note.title,
                initialContent = current.note.content,
                busy = state.busy,
                error = state.error,
                onCancel = { mode = NotasMode.List },
                onSave = { title, content ->
                    onUpdate(current.note.id, title, content) { mode = NotasMode.List }
                },
            )
        }
    }

    pendingDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Eliminar nota") },
            text = { Text("¿Eliminar “${note.title}”?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(note.id)
                        pendingDelete = null
                    },
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun NotasList(
    padding: PaddingValues,
    state: NotasUiState,
    onEdit: (NoteDto) -> Unit,
    onDeleteRequest: (NoteDto) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
    ) {
        Text("Notas", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        when {
            state.loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            state.error != null -> {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }

            state.notes.isEmpty() -> {
                Text("Aún no hay notas. Crea la primera.")
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.notes, key = { it.id }) { note ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.content.ifBlank { "Sin contenido" },
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    IconButton(onClick = { onEdit(note) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { onDeleteRequest(note) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Borrar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteEditor(
    padding: PaddingValues,
    titleLabel: String,
    initialTitle: String,
    initialContent: String,
    busy: Boolean,
    error: String?,
    onCancel: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var content by remember(initialContent) { mutableStateOf(initialContent) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
    ) {
        Text(titleLabel, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Contenido") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onSave(title.trim(), content) },
                enabled = !busy && title.isNotBlank(),
            ) {
                Text(if (busy) "Guardando…" else "Guardar")
            }
            OutlinedButton(onClick = onCancel, enabled = !busy) {
                Text("Cancelar")
            }
        }
    }
}
