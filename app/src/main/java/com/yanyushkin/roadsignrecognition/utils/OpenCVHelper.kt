package com.yanyushkin.roadsignrecognition.utils

import android.graphics.Bitmap
import android.util.Log
import com.yanyushkin.roadsignrecognition.extensions.toBitmap
import com.yanyushkin.roadsignrecognition.extensions.toMat
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

object OpenCVHelper {

    private val minValues = Scalar(117.0, 100.0, 45.0)
    private val maxValues = Scalar(255.0, 255.0, 255.0)

    fun findSign(sourceBitmap: Bitmap): Bitmap? {
        val rotatedBitmap = rotateBitmap(sourceBitmap)
        val sourceMat = rotatedBitmap.toMat()

        val hsv = toHSV(sourceMat)
        val frame = bin(hsv)
        Imgproc.Canny(frame, frame, 100.0, 200.0)
        val contours = getContours(frame)

        var sign: Bitmap? = null
        if (contours.size > 0) {
            val boundingRect = Imgproc.boundingRect(contours[0])

            Log.d("CONTOURS", boundingRect.toString())
            if (boundingRect.width > 60 && boundingRect.height > 60)
            sign = Mat(sourceMat, boundingRect).toBitmap()
        }
        return sign
    }

    private fun toHSV(sourceMat: Mat): Mat {
        val hsv = Mat()
        Imgproc.cvtColor(sourceMat, hsv, Imgproc.COLOR_RGB2HSV)
        return hsv
    }

    private fun bin(hsv: Mat): Mat {
        val frame = Mat()
        Core.inRange(hsv, minValues, maxValues, frame)
        return frame
    }

    private fun getContours(frame: Mat): ArrayList<MatOfPoint> {
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(
            frame.clone(),
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        contours.sortByDescending { contour -> Imgproc.contourArea(contour) }
        return contours
    }
}