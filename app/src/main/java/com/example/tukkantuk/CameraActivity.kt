package com.example.tukkantuk

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.media.SoundPool
import android.os.Build

//import org.opencv.core.Mat
//import org.opencv.core.MatOfRect
//import org.opencv.core.Rect
//import org.opencv.core.Scalar
//import org.opencv.imgproc.Imgproc
import android.os.Bundle
//import org.opencv.core.CascadeClassifier
//import org.opencv.android.Utils
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.tukkantuk.databinding.ActivityCameraBinding
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


class  CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var tfliteClassifier: TFLiteClassifier
    private var closedEyesStartTime: Long = 0
    private var closedEyesDuration: Long = 0
    private var status: String = "Tidak Fokus"
    private var soundId: Int = 0
    private var spLoaded = false
    private var sp: SoundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .build()

//    private lateinit var imageClassifierHelper: ImageClassifierHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
            spLoaded = true
        } else {
            Toast.makeText(this@CameraActivity, "Gagal Load", Toast.LENGTH_SHORT).show()
            }
        }
        soundId = sp.load(this, R.raw.fokus, 1)

        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            startCamera()
//            setupNotificationChannel()
//            loadHaarCascade()
        }
//        binding.captureImage.setOnClickListener { takePhoto() }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA



    @OptIn(ExperimentalGetImage::class) private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        tfliteClassifier = TFLiteClassifier(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        // Convert the image to ByteBuffer as expected by the TFLite model
                        val byteBuffer =
                            imageProxy.image?.let { it1 -> convertImageToByteBuffer(it1) }
                        // Run the classification
                        val prediction = byteBuffer?.let { it1 -> tfliteClassifier.classify(it1) }
                        var percentage = 0 // Default percentage

                        if (prediction != null) {
                            percentage = (prediction * 100).toInt()
                            status = checkEyeStatus(percentage)
//                            if (status == "Tidak Fokus"){
//                                playSound()
//                            }
                        }



                        runOnUiThread {
                            binding.percentageTextView.text = "Percentage: $percentage%"
                            binding.statusTextView.text = status
                        }

                        // Close the image to prevent memory leaks
                        imageProxy.close()
                    }
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
//                    imageCapture,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")

            }
//            try {
//                imageAnalysis
//            } catch (e: Exception){
//                Log.e(TAG, "Error during image analysis", e)
//            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun playSound() {
        if (spLoaded) {
            sp.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        sp.release()
    }



    private fun showNotification() {
        var soundId: Int = 0
        var spLoaded = false
        var sp: SoundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .build()



    }
    private fun checkEyeStatus(predictions: Int): String {
        if (predictions > 50) { // Assuming predictions > 0.5 indicates closed eyes
            status = "Tidak Fokus"
            if (closedEyesStartTime == 0L) {
                // Record the start time when eyes are first detected as closed
                closedEyesStartTime = System.currentTimeMillis()
            } else {
                // Calculate how long the eyes have been closed
                closedEyesDuration = System.currentTimeMillis() - closedEyesStartTime
                if (closedEyesDuration > 1200) { // 1200 milliseconds equals 1 seconds
                    playSound()

                    // Reset the timer after showing the notification
                    closedEyesStartTime = 0L
                }
//                else {
//                    status = "Fokus"
//
//                }
            }
        } else {

            status = "Fokus"
            closedEyesStartTime = 0L
            closedEyesDuration = 0L
        }
        return status
    }




    private fun convertImageToByteBuffer(image: Image): ByteBuffer {
        // Implement this method based on your model's input requirements
        val planes = image.planes
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // Resize and convert to grayscale if necessary, similar to your Python code
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(224 * 224)

        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
        var pixel = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val `val` = intValues[pixel++] // RGBA
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((`val` and 0xFF) / 255.0f))
            }
        }

        return byteBuffer
    // This may include resizing the image, converting it to grayscale, normalizing, etc.
    }


    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
        private const val notificationId = 101
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "TukKantuk"
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }
                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture?.targetRotation = rotation
            }
        }
    }
    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

//    private fun loadHaarCascade() {
//        try {
//            // Load the cascade file from the application resources
//            val `is` = resources.assets.open("haarcascade_eye.xml")
//            val cascadeDir = getDir("cascade", MODE_PRIVATE)
//            val cascadeFile = File(cascadeDir, "haarcascade_eye.xml")
//            val os = FileOutputStream(cascadeFile)
//            val buffer = ByteArray(4096)
//            var bytesRead: Int
//            while (`is`.read(buffer).also { bytesRead = it } != -1) {
//                os.write(buffer, 0, bytesRead)
//            }
//            `is`.close()
//            os.close()
//
//            // Load the cascade classifier
//            eyeCascade = CascadeClassifier(cascadeFile.absolutePath)
//            if (eyeCascade.empty()) {
//                Log.e(TAG, "Failed to load cascade classifier")
//                eyeCascade = null
//            } else {
//                Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.absolutePath)
//            }
//            cascadeDir.delete()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.e(TAG, "Failed to load cascade. Exception thrown: $e")
//        }
//    }

}
