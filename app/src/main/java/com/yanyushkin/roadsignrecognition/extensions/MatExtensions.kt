package com.yanyushkin.roadsignrecognition.extensions

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat

fun Mat.toBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    val bitmap = Bitmap.createBitmap(this.cols(), this.rows(), config)
    Utils.matToBitmap(this, bitmap)
    return bitmap
}