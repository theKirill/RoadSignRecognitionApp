package com.yanyushkin.roadsignrecognition.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.yanyushkin.roadsignrecognition.IMAGE_HEIGHT
import com.yanyushkin.roadsignrecognition.IMAGE_WIDTH
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun scaleBitmap(sourceBitmap: Bitmap): Bitmap =
    Bitmap.createScaledBitmap(sourceBitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true)

fun prepareBitmap(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
    val batchNum = 0
    val result = Array(1) { Array(IMAGE_WIDTH) { Array(IMAGE_HEIGHT) { FloatArray(3) } } }
    for (x in 0 until IMAGE_WIDTH) {
        for (y in 0 until IMAGE_HEIGHT) {
            val pixel = bitmap.getPixel(x, y)
            result[batchNum][x][y][0] = (Color.red(pixel) - 127) / 255.0f
            result[batchNum][x][y][1] = (Color.green(pixel) - 127) / 255.0f
            result[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 255.0f
        }
    }
    return result
}

fun getIndexOfMaxElem(array: FloatArray): Int {
    val maxIndex = array.indices.maxBy { array[it] }
    return maxIndex!!
}
