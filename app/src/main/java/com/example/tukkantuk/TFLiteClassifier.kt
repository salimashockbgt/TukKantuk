package com.example.tukkantuk

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class TFLiteClassifier(context: Context) {
    private var interpreter: Interpreter? = null

    init {
        val model = loadModelFile(context, "drowsy_detection.tflite")
        interpreter = Interpreter(model)
    }

    private fun loadModelFile(context: Context, filename: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(image: ByteBuffer): Float {
        // Adjust the size of the output array to match the model's output tensor shape [1, 1]
        val output = Array(1) { FloatArray(1) }

        try {
            interpreter?.run(image, output)
            Log.d(TAG, "Inference ran successfully, output: ${output[0][0]}")
        } catch (e: Exception) {
            Log.e(TAG, "Error during inference", e)
        }
        // Return the first element of the output array, which contains the prediction
        return output[0][0]
    }


    fun close() {
        interpreter?.close()
    }
}
