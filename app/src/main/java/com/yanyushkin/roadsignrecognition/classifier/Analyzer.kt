package com.yanyushkin.roadsignrecognition.classifier

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.util.TimeUnit
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.yanyushkin.roadsignrecognition.extensions.toBitmap
import com.yanyushkin.roadsignrecognition.states.ScreenState
import com.yanyushkin.roadsignrecognition.utils.*
import java.nio.ByteBuffer

class Analyzer(private val context: Context) : ImageAnalysis.Analyzer {

    private var lastAnalyzedTimestamp = 0L
    private val classifier: Classifier
    val state = MutableLiveData<Int>()
    val stateBmp = MutableLiveData<Bitmap>()

    init {
        classifier = Classifier(context)
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >=
            java.util.concurrent.TimeUnit.SECONDS.toMillis(3)
        ) {
            // Since format in ImageAnalysis is YUV, image.planes[0]
            // contains the Y (luminance) plane
            //val buffer = image.planes[0].buffer
            var img = image.image
            val matrix = Matrix()
            img?.let {
                val b = image.image!!.toBitmap()
                val bm = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)

                val sign = OpenCVHelper.findSign(bm)
                Log.d("SUKA", sign.toString())
                sign?.let {
                    val scaledBitmap = scaleBitmap(sign)
                    val signClass = classifier.classify(scaledBitmap)

                    stateBmp.postValue(scaledBitmap)
                    state.postValue(signClass)
                }
            }
            // Extract image data from callback object
            //val data = buffer.toByteArray()
            // Convert the data into an array of pixel values
            //val pixels = BitmapFactory.decodeByteArray(data, 0, data.size);
            // Compute average luminance for the image
            //val pixels = getOutputImage(buffer)
            // Log the new luma value


            // Update timestamp of last analyzed frame
            lastAnalyzedTimestamp = currentTimestamp
        }
        image.close()
    }
}