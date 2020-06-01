package com.yanyushkin.roadsignrecognition.ui.photo

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanyushkin.roadsignrecognition.App
import com.yanyushkin.roadsignrecognition.classifier.Classifier
import com.yanyushkin.roadsignrecognition.domain.RoadSignInfo
import com.yanyushkin.roadsignrecognition.extensions.toBitmap
import com.yanyushkin.roadsignrecognition.extensions.toMat
import com.yanyushkin.roadsignrecognition.network.RoadSignInfoRepository
import com.yanyushkin.roadsignrecognition.states.ScreenState
import com.yanyushkin.roadsignrecognition.utils.scaleBitmap
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.net.UnknownHostException
import javax.inject.Inject

class PhotoVM(context: Context) : ViewModel() {

    @Inject
    lateinit var repository: RoadSignInfoRepository
    val state = MutableLiveData<ScreenState>()
    val sign = MutableLiveData<RoadSignInfo>()
    val kek = MutableLiveData<Bitmap>()
    private val classifier: Classifier

    init {
        App.component.injectsPhotoVM(this)
        classifier = Classifier(context)
    }

    fun classify(sourceBitmap: Bitmap) {
        kek(sourceBitmap)
        val scaledBitmap = scaleBitmap(sourceBitmap)
        val result = classifier.classify(scaledBitmap)
        getSignInfo(result)
    }

    private fun getSignInfo(id: Int) =
        repository.getSignInfo(id).subscribe({
            sign.value = it.result!!.transform()
            state.value = ScreenState.SUCCESS
        }, {
            if (it is UnknownHostException)
                state.value = ScreenState.ERROR_NO_INTERNET
            else
                state.value = ScreenState.ERROR_OTHER
        })

    private fun kek(bmp: Bitmap){
        val mat = bmp.toMat()
        val hsv = Mat()
        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV)
        val minValues = Scalar(117.0, 100.0, 45.0)
        val maxValues = Scalar(255.0, 255.0, 255.0)
        val frame = Mat()
        Core.inRange(hsv, minValues, maxValues, frame)
        kek.value = frame.toBitmap()
    }
}