package com.yanyushkin.roadsignrecognition.utils

import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import com.yanyushkin.roadsignrecognition.IMAGE_HEIGHT
import com.yanyushkin.roadsignrecognition.IMAGE_WIDTH
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.ByteArrayOutputStream
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

fun rotateBitmap(bitmap: Bitmap): Bitmap {
    val matrixMAIN = Matrix()
    matrixMAIN.postRotate(90f)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrixMAIN, true)
}

fun getIndexOfMaxElem(array: FloatArray): Int = array.indices.maxBy { array[it] }!!

fun getOutputImage(output: ByteBuffer): Bitmap {
    output.rewind() // Rewind the output buffer after running.

    val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(32 * 32) // Set your expected output's height and width
    for (i in 0 until 32 * 32) {
        val a = 0xFF
        val r: Float = output?.float!! * 255.0f
        val g: Float = output?.float!! * 255.0f
        val b: Float = output?.float!! * 255.0f
        pixels[i] = a shl 24 or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
    }
    bitmap.setPixels(pixels, 0, 32, 0, 0, 32, 32)

    return bitmap
}

fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}