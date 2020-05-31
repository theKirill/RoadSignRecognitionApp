package com.yanyushkin.roadsignrecognition.classifier

import android.content.Context
import android.icu.util.TimeUnit
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

class Analyzer(private val context: Context) : ImageAnalysis.Analyzer {

    private var lastAnalyzedTimestamp = 0L

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >=
            java.util.concurrent.TimeUnit.SECONDS.toMillis(1)) {
            // Since format in ImageAnalysis is YUV, image.planes[0]
            // contains the Y (luminance) plane
            val buffer = image.planes[0].buffer
            // Extract image data from callback object
            val data = buffer.toByteArray()
            // Convert the data into an array of pixel values
            val pixels = data.map { it.toInt() and 0xFF }
            // Compute average luminance for the image
            val luma = pixels.average()
            // Log the new luma value
            Log.d("CameraXApp", "Average luminosity: $luma")
            // Update timestamp of last analyzed frame
            lastAnalyzedTimestamp = currentTimestamp
            showToast()
        }
        image.close()
    }

    private fun showToast() {
        Runnable() {
            Toast.makeText(context, "KEK", Toast.LENGTH_LONG).show()
        }
    }
}