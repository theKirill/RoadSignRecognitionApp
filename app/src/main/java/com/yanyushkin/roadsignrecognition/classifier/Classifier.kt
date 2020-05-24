package com.yanyushkin.roadsignrecognition.classifier

import android.content.Context
import android.graphics.Bitmap
import com.yanyushkin.roadsignrecognition.MODEL_NAME
import com.yanyushkin.roadsignrecognition.NUM_CLASSES
import com.yanyushkin.roadsignrecognition.utils.getIndexOfMaxElem
import com.yanyushkin.roadsignrecognition.utils.prepareBitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class Classifier(private val context: Context) {

    private lateinit var interpreter: Interpreter

    init {
        initInterpreter()
    }

    private fun initInterpreter() {
        val assetManager = context.assets
        val fd = assetManager.openFd(MODEL_NAME)
        val fis = FileInputStream(fd.fileDescriptor)
        val channel = fis.channel
        val offset = fd.startOffset
        val length = fd.declaredLength

        val tfLiteModel = channel.map(FileChannel.MapMode.READ_ONLY, offset, length) as ByteBuffer

        val options = Interpreter.Options()
        options.setUseNNAPI(true)

        interpreter = Interpreter(tfLiteModel, options)
    }

    fun classify(bitmap: Bitmap): Int {
        val input = prepareBitmap(bitmap)
        val result = Array(1) { FloatArray(NUM_CLASSES) }

        interpreter.run(input, result)

        return getIndexOfMaxElem(result[0])
    }
}