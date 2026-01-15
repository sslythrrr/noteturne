package com.sslythrrr.noteturne

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import androidx.core.content.edit

class NotesRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("noteturne_prefs", Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        private const val KEY_NOTES = "notes_list"
    }

    suspend fun getAllNotes(): List<Note> = withContext(Dispatchers.IO) {
        val notesJson = sharedPreferences.getString(KEY_NOTES, null)
        if (notesJson.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<Note>>(notesJson)
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun saveNote(note: Note) = withContext(Dispatchers.IO) {
        val currentNotes = getAllNotes().toMutableList()
        currentNotes.add(0, note)
        saveAllNotes(currentNotes)
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        val currentNotes = getAllNotes().toMutableList()
        val index = currentNotes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            currentNotes[index] = note
            saveAllNotes(currentNotes)
        }
    }

    suspend fun deleteNote(id: String) = withContext(Dispatchers.IO) {
        val currentNotes = getAllNotes().toMutableList()
        currentNotes.removeIf { it.id == id }
        saveAllNotes(currentNotes)
    }

    private fun saveAllNotes(notes: List<Note>) {
        val notesJson = json.encodeToString(notes)
        sharedPreferences.edit { putString(KEY_NOTES, notesJson) }
    }
}