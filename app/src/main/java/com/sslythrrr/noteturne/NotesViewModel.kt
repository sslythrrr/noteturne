package com.sslythrrr.noteturne

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(application)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _notes.value = repository.getAllNotes()
        }
    }

    fun addNote(content: String) {
        viewModelScope.launch {
            val seed = Enigma.generateSeed()
            val encoded = Enigma.encode(content, seed)
            val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date())

            val note = Note(
                id = UUID.randomUUID().toString(),
                encodedContent = encoded,
                decodedContent = content,
                encryptionSeed = seed,
                timestamp = timestamp,
                isDecoded = false
            )

            repository.saveNote(note)
            loadNotes()
        }
    }

    fun updateNote(noteId: String, newContent: String) {
        viewModelScope.launch {
            val currentNotes = _notes.value
            val noteToUpdate = currentNotes.find { it.id == noteId } ?: return@launch

            val encoded = Enigma.encode(newContent, noteToUpdate.encryptionSeed)

            val updatedNote = noteToUpdate.copy(
                encodedContent = encoded,
                decodedContent = newContent,
                isDecoded = false
            )

            repository.updateNote(updatedNote)
            loadNotes()
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
            loadNotes()
        }
    }

    fun toggleNoteVisibility(id: String) {
        viewModelScope.launch {
            val currentNotes = _notes.value.toMutableList()
            val index = currentNotes.indexOfFirst { it.id == id }

            if (index != -1) {
                currentNotes[index] = currentNotes[index].copy(
                    isDecoded = !currentNotes[index].isDecoded
                )
                _notes.value = currentNotes
            }
        }
    }

    fun exportNote(note: Note): NoteExport {
        return NoteExport(
            version = "1.0",
            encryptedContent = note.encodedContent,
            encryptionSeed = note.encryptionSeed,
            exportedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(Date()),
            originalTimestamp = note.timestamp
        )
    }

    fun importNote(noteExport: NoteExport) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date())

            val decoded = Enigma.decode(
                noteExport.encryptedContent,
                noteExport.encryptionSeed
            )

            val note = Note(
                id = UUID.randomUUID().toString(),
                encodedContent = noteExport.encryptedContent,
                decodedContent = decoded,
                encryptionSeed = noteExport.encryptionSeed,
                timestamp = timestamp,
                isDecoded = false
            )

            repository.saveNote(note)
            loadNotes()
        }
    }
}