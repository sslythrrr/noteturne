package com.sslythrrr.noteturne

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String,
    val encodedContent: String,
    val decodedContent: String,
    val encryptionSeed: String,
    val timestamp: String,
    val isDecoded: Boolean = false
)

@Serializable
data class NoteExport(
    val version: String = "1.0",
    val encryptedContent: String,
    val encryptionSeed: String,
    val exportedAt: String,
    val originalTimestamp: String? = null
)