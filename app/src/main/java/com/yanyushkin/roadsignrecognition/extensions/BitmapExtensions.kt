package com.yanyushkin.roadsignrecognition.extensions

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat

fun Bitmap.toMat(): Mat {
    val mat = Mat()
    Utils.bitmapToMat(this, mat)
    return mat
}