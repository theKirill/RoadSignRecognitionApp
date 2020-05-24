package com.yanyushkin.roadsignrecognition.ui.photo

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanyushkin.roadsignrecognition.App
import com.yanyushkin.roadsignrecognition.classifier.Classifier
import com.yanyushkin.roadsignrecognition.domain.RoadSignInfo
import com.yanyushkin.roadsignrecognition.network.RoadSignInfoRepository
import com.yanyushkin.roadsignrecognition.states.ScreenState
import com.yanyushkin.roadsignrecognition.utils.scaleBitmap
import java.net.UnknownHostException
import javax.inject.Inject

class PhotoVM(context: Context) : ViewModel() {

    @Inject
    lateinit var repository: RoadSignInfoRepository
    val state = MutableLiveData<ScreenState>()
    val sign = MutableLiveData<RoadSignInfo>()
    private val classifier: Classifier

    init {
        App.component.injectsPhotoVM(this)
        classifier = Classifier(context)
    }

    fun classify(sourceBitmap: Bitmap) {
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
}