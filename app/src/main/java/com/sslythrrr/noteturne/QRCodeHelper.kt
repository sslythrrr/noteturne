package com.sslythrrr.noteturne

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import androidx.core.graphics.createBitmap

object QRCodeHelper {

    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix[x, y]) {
                        0xFF000000.toInt()
                    } else {
                        0xFFFFFFFF.toInt()
                    }
                }
            }

            createBitmap(width, height).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}