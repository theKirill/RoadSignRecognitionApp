package com.yanyushkin.roadsignrecognition.ui.photo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.util.TimingLogger
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanyushkin.roadsignrecognition.App
import com.yanyushkin.roadsignrecognition.classifier.Classifier
import com.yanyushkin.roadsignrecognition.domain.RoadSignInfo
import com.yanyushkin.roadsignrecognition.extensions.toBitmap
import com.yanyushkin.roadsignrecognition.extensions.toMat
import com.yanyushkin.roadsignrecognition.network.RoadSignInfoRepository
import com.yanyushkin.roadsignrecognition.states.ScreenState
import com.yanyushkin.roadsignrecognition.utils.OpenCVHelper
import com.yanyushkin.roadsignrecognition.utils.scaleBitmap
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.net.UnknownHostException
import javax.inject.Inject

class PhotoVM(context: Context) : ViewModel() {

    @Inject
    lateinit var repository: RoadSignInfoRepository
    val state = MutableLiveData<ScreenState>()
    val signInfo = MutableLiveData<RoadSignInfo>()
    val signBitmap = MutableLiveData<Bitmap>()
    private val classifier: Classifier

    init {
        App.component.injectsPhotoVM(this)
        classifier = Classifier(context)
    }

    fun classify(sourceBitmap: Bitmap) {
        val sign = OpenCVHelper.findSign(sourceBitmap)

        if (sign != null) {
            signBitmap.value = sign
            val scaledBitmap = scaleBitmap(sign!!)
            val signClass = classifier.classify(scaledBitmap)
            getSignInfo(signClass)
        } else {
            state.value = ScreenState.NOT_SIGN
        }
    }

    @SuppressLint("CheckResult")
    private fun getSignInfo(id: Int) {
        repository.getSignInfo(id).subscribe({
            signInfo.value = it.result!!.transform()
            state.value = ScreenState.SUCCESS
        }, {
            if (it is UnknownHostException)
                state.value = ScreenState.ERROR_NO_INTERNET
            else
                state.value = ScreenState.ERROR_OTHER
        })
    }
}