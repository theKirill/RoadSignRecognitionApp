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
import com.yanyushkin.roadsignrecognition.App
import com.yanyushkin.roadsignrecognition.extensions.toBitmap
import com.yanyushkin.roadsignrecognition.network.RoadSignInfoRepository
import com.yanyushkin.roadsignrecognition.states.ScreenState
import com.yanyushkin.roadsignrecognition.utils.*
import java.net.UnknownHostException
import java.nio.ByteBuffer
import javax.inject.Inject

class Analyzer(private val context: Context) : ImageAnalysis.Analyzer {

    @Inject
    lateinit var repository: RoadSignInfoRepository
    private var lastAnalyzedTimestamp = 0L
    private val classifier: Classifier
    val stateSignInfo = MutableLiveData<String>()
    val stateBmp = MutableLiveData<Bitmap>()

    init {
        App.component.injectsAnalyzer(this)
        classifier = Classifier(context)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >=
            java.util.concurrent.TimeUnit.SECONDS.toMillis(1)
        ) {
            val img = image.image
            img?.let {
                val bmp = image.image!!.toBitmap()
                val sign = OpenCVHelper.findSign(bmp)
                sign?.let {
                    val scaledBitmap = scaleBitmap(sign)
                    stateBmp.postValue(scaledBitmap)
                    val signClass = classifier.classify(scaledBitmap)
                    getSignInfo(signClass)
                }
            }
            lastAnalyzedTimestamp = currentTimestamp
        }
        image.close()
    }

    private fun getSignInfo(id: Int) =
        repository.getSignInfo(id).subscribe({
            val info = it.result!!.transform()
            stateSignInfo.postValue(info.name + "\n" + info.importantInfo)
        }, {

        })
}