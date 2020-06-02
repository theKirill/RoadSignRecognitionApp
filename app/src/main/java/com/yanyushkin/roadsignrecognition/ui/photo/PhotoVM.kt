package com.yanyushkin.roadsignrecognition.ui.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
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
import org.opencv.core.*
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
        val newBitmap = kek(sourceBitmap)
        val scaledBitmap = scaleBitmap(newBitmap)
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

    private fun kek(bmp: Bitmap): Bitmap {
        val matrixMAIN = Matrix()
        matrixMAIN.postRotate(90f)
        val bmpMain = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrixMAIN, true)

        val mat = bmpMain.toMat()
        val hsv = Mat()
        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV)
        val minValues = Scalar(117.0, 100.0, 45.0)
        val maxValues = Scalar(255.0, 255.0, 255.0)
        val frame = Mat()
        Core.inRange(hsv, minValues, maxValues, frame)

        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(frame.clone(), contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        contours.sortByDescending { contour -> Imgproc.contourArea(contour) }

        val boundingRect = Imgproc.boundingRect(contours[0])

        /*Imgproc.rectangle(mat,
            Point(boundingRect.x.toDouble(), boundingRect.y.toDouble()),
            Point(boundingRect.x+boundingRect.width.toDouble(),
                boundingRect.x+boundingRect.height.toDouble()),
            Scalar(0.0, 0.0, 255.0),5)*/


        val sign = Mat(mat, boundingRect)

        var bmp = sign.toBitmap()
        val matrix = Matrix()
        matrix.postRotate(90f)
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)

        var bmp2 = mat.toBitmap()
        val matrix2 = Matrix()
        matrix2.postRotate(90f)
        bmp2 = Bitmap.createBitmap(bmp2, 0, 0, bmp2.width, bmp2.height, matrix2, true)
        kek.value = sign.toBitmap()

        return bmp
    }
}