package com.example.notes

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notes.ui.theme.NotesTheme
import androidx.compose.ui.Alignment

// Užrašo duomenų klasė su ID, pavadinimu ir tekstu
data class Note(
    val id: Long,
    val title: String,
    val body: String
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesTheme {
                NotesScreen()
            }
        }
    }
    @Composable
    fun NotesScreen() {
        // Kintamieji užrašo pavadinimui, turiniui ir sąrašui
        var title by remember { mutableStateOf("") }
        var body by remember { mutableStateOf("") }
        var notesList by remember { mutableStateOf(loadNotes()) }

        // Kintamieji redagavimui
        var isEditing by remember { mutableStateOf(false) }
        var editingNoteId by remember { mutableStateOf<Long?>(null) }

        Column(modifier = Modifier.padding(19.dp)) {
            Spacer(modifier = Modifier.height(150.dp))
            Text(
                text = "1 Užduotis", // Programos pavadinimas/antraštė
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            // TITLE įvedimas
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Pavadinimas") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // BODY TEZT įvedimas
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Tekstas") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Pridėti užrašą arba atnaujinti
            Button(
                onClick = {
                    if (title.isNotBlank() || body.isNotBlank()) {
                        if (isEditing && editingNoteId != null) {
                            updateNote(editingNoteId!!, title, body) // Atnaujina
                        } else {
                            saveNote(title, body) // Išsaugo
                        }
                        // Išvalo įvesties laukus ir nutraukia redagavimą
                        title = ""
                        body = ""
                        isEditing = false
                        editingNoteId = null
                        notesList = loadNotes() // Perkrauna užrašų sąrašą
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Naujinti" else "Pridėti")
            }

            // Atšaukimo mygtukas redagavimo būsenoj
            if (isEditing) {
                TextButton(
                    onClick = {
                        isEditing = false
                        editingNoteId = null
                        title = ""
                        body = ""
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Atšaukti")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Užrašai", style = MaterialTheme.typography.titleLarge) // antraštė

            // Sąrašas užrašų rodymui
            LazyColumn {
                items(notesList) { note ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {}
                    ) { Text(text = "${note.title}", style = MaterialTheme.typography.titleMedium) // Užrašo pavadinimas
                        Text(text = note.body, style = MaterialTheme.typography.bodyMedium) // Užrašo turinys
                        Row {
                            // Redagavimo mygtukas
                            TextButton(onClick = {
                                title = note.title
                                body = note.body
                                editingNoteId = note.id
                                isEditing = true
                            }) {
                                Text("Redaguoti")
                            }
                            // Trinimo mygtukas
                            TextButton(onClick = {
                                deleteNote(note.id)
                                notesList = loadNotes()
                            }) {
                                Text("Trinti")
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }
    // Atnaujina esamą užrašą DB
    private fun updateNote(id: Long, newTitle: String, newBody: String) {
        val values = ContentValues().apply {
            put(NotesDb.COL_TITLE, newTitle)
            put(NotesDb.COL_BODY, newBody)
        }
        contentResolver.update(
            NotesProvider.CONTENT_URI,
            values,
            "${NotesDb.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }
    // Išsaugo naują užrašą duomenų bazėje
    private fun saveNote(title: String, body: String) {
        val values = ContentValues().apply {
            put(NotesDb.COL_TITLE, title)
            put(NotesDb.COL_BODY, body)
        }
        contentResolver.insert(NotesProvider.CONTENT_URI, values)
    }

    // Ištrina užrašą iš DB pagal ID
    private fun deleteNote(id: Long) {
        contentResolver.delete(
            NotesProvider.CONTENT_URI,
            "${NotesDb.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    // Užkrauna visus užrašus iš DB ir grąžina juos sąrašu
    private fun loadNotes(): List<Note> {
        val cursor: Cursor? = contentResolver.query(
            NotesProvider.CONTENT_URI,
            null, null, null, null
        )
        val notes = mutableListOf<Note>()
        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(NotesDb.COL_ID)
            val titleIndex = it.getColumnIndexOrThrow(NotesDb.COL_TITLE)
            val bodyIndex = it.getColumnIndexOrThrow(NotesDb.COL_BODY)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val title = it.getString(titleIndex)
                val body = it.getString(bodyIndex)
                notes.add(Note(id, title, body))
            }
        }
        return notes
    }
}
