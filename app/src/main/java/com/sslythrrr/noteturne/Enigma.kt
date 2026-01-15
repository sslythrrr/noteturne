package com.sslythrrr.noteturne

import java.util.UUID
import kotlin.math.absoluteValue

object Enigma {

    fun generateSeed(): String {
        return UUID.randomUUID().toString().take(16)
    }

    fun encode(text: String, seed: String): String {
        val hexText = text.toByteArray(Charsets.UTF_8)
            .joinToString("") { byte -> "%02X".format(byte) }

        return enigmaEncrypt(hexText, seed)
    }

    fun decode(text: String, seed: String): String {
        val decryptedHex = enigmaDecrypt(text, seed)

        return try {
            val bytes = decryptedHex.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            decryptedHex
        }
    }

    private val ROTORS = listOf(
        "EKMFLGDQVZNTOWYHXUSPAIBRCJ0123456789",
        "AJDKSIRUXBLHWTMCQGZNPYFVOE9876543210",
        "BDFHJLCPRTXVZNYEIWGAKMUSQO1357924680",
        "ESOVPZJAYQUIRHXLNFTGKDCMWB2468013579",
        "VZBRGITYUPSDNHLXAWMJQOFECK9182736450"
    )

    private val REFLECTOR = "YRUHQSLDPXNGOKMIEBFZCWVJAT9876543210"

    private const val CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private const val CHARSET_SIZE = 36

    private fun Int.posMod(n: Int): Int {
        val result = this % n
        return if (result < 0) result + n else result
    }

    private fun enigmaEncrypt(text: String, seed: String): String {
        val rotorPositions = initializeRotorPositions(seed)
        val result = StringBuilder()

        for (char in text) {
            val upperChar = char.uppercaseChar()
            if (upperChar in CHARSET) {
                stepRotors(rotorPositions)
                val encrypted = encryptChar(upperChar, rotorPositions)
                result.append(encrypted)
            } else {
                result.append(char)
            }
        }

        return result.toString()
    }

    private fun enigmaDecrypt(text: String, seed: String): String {
        return enigmaEncrypt(text, seed)
    }

    private fun initializeRotorPositions(seed: String): IntArray {
        val positions = IntArray(5)

        for (i in 0 until 5) {
            val seedChar = seed.getOrNull(i) ?: 'A'
            positions[i] = ((seedChar.code xor seed.hashCode()).absoluteValue % CHARSET_SIZE)
        }

        return positions
    }

    private fun stepRotors(positions: IntArray) {
        positions[0] = (positions[0] + 1) % CHARSET_SIZE

        if (positions[0] == 0) {
            positions[1] = (positions[1] + 1) % CHARSET_SIZE

            if (positions[1] == 0) {
                positions[2] = (positions[2] + 1) % CHARSET_SIZE

                if (positions[2] == 0) {
                    positions[3] = (positions[3] + 1) % CHARSET_SIZE

                    if (positions[3] == 0) {
                        positions[4] = (positions[4] + 1) % CHARSET_SIZE
                    }
                }
            }
        }
    }

    private fun encryptChar(char: Char, positions: IntArray): Char {
        var index = CHARSET.indexOf(char)
        if (index == -1) return char

        // Rotor 1
        index = (index + positions[0]).posMod(CHARSET_SIZE)
        index = CHARSET.indexOf(ROTORS[0][index])

        // Rotor 2
        index = (index + positions[1] - positions[0]).posMod(CHARSET_SIZE)
        index = CHARSET.indexOf(ROTORS[1][index])

        // Rotor 3
        index = (index + positions[2] - positions[1]).posMod(CHARSET_SIZE)
        index = CHARSET.indexOf(ROTORS[2][index])

        // Rotor 4
        index = (index + positions[3] - positions[2]).posMod(CHARSET_SIZE)
        index = CHARSET.indexOf(ROTORS[3][index])

        // Rotor 5
        index = (index + positions[4] - positions[3]).posMod(CHARSET_SIZE)
        index = CHARSET.indexOf(ROTORS[4][index])

        // Reflector
        index = (index - positions[4]).posMod(CHARSET_SIZE)
        index = CHARSET.indexOf(REFLECTOR[index])

        // Rotor 5 (reverse)
        index = (index + positions[4]).posMod(CHARSET_SIZE)
        index = ROTORS[4].indexOf(CHARSET[index])

        // Rotor 4 (reverse)
        index = (index + positions[3] - positions[4]).posMod(CHARSET_SIZE)
        index = ROTORS[3].indexOf(CHARSET[index])

        // Rotor 3 (reverse)
        index = (index + positions[2] - positions[3]).posMod(CHARSET_SIZE)
        index = ROTORS[2].indexOf(CHARSET[index])

        // Rotor 2 (reverse)
        index = (index + positions[1] - positions[2]).posMod(CHARSET_SIZE)
        index = ROTORS[1].indexOf(CHARSET[index])

        // Rotor 1 (reverse)
        index = (index + positions[0] - positions[1]).posMod(CHARSET_SIZE)
        index = ROTORS[0].indexOf(CHARSET[index])

        index = (index - positions[0]).posMod(CHARSET_SIZE)

        return CHARSET[index]
    }
}