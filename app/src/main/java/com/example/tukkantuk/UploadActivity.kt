package com.example.tukkantuk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.tukkantuk.CameraActivity.Companion.CAMERAX_RESULT
import com.example.tukkantuk.databinding.ActivityUploadBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentUri: Uri? = null
    private val viewModel by viewModels<ViewModelUpload> {
        ViewModelFactory.getInstance(this)
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(arrayOf(REQUIRED_PERMISSION))
        } else {
            getCurrentLocation()
        }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.cameraXButton.setOnClickListener { startCameraX() }
    }
    private fun getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            showToast(getString(R.string.location_permission_required))
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                }
            }
    }



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            showToast(getString(R.string.all_permissions_granted))
            getCurrentLocation()
        } else {
            showToast(getString(R.string.permissions_denied))
        }
    }



    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun startCamera() {
        currentUri = getImageUri(this)
        launcherIntentCamera.launch(currentUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }


    private fun uploadImage() {
        currentUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descriptionEditText.text.toString()
            if (currentLatitude != null && currentLongitude != null) {
                viewModel.uploadStory(imageFile, description, currentLatitude!!, currentLongitude!!)
                    .observe(this) { result ->
                        if (result != null) {
                            showLoading(true)
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }

                                is Result.Success -> {
                                    showToast(result.data.message)
                                    showLoading(false)
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }

                                is Result.Error -> {
                                    showToast(getString(R.string.upload_gagal))
                                    showLoading(false)
                                }

                                else -> {}
                            }
                        }
                    }
            }
        } ?: showToast(getString(R.string.empty_image))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val MAXIMAL_SIZE = 1000000
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }


//    private fun getImageUri(context: Context): Uri {
//        var uri: Uri? = null
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val contentValues = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
//                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
//            }
//            uri = context.contentResolver.insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues
//            )
//        }
//        return uri ?: getImageUriForPreQ(context)
//    }
//
//    private fun getImageUriForPreQ(context: Context): Uri {
//        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
//        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
//        return FileProvider.getUriForFile(
//            context,
//            "${BuildConfig.APPLICATION_ID}.fileprovider",
//            imageFile
//        )
//    }
//
//    private fun createCustomTempFile(context: Context): File {
//        val filesDir = context.externalCacheDir
//        return File.createTempFile(timeStamp, ".jpg", filesDir)
//    }
//
//    private fun uriToFile(imageUri: Uri, context: Context): File {
//        val myFile = createCustomTempFile(context)
//        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
//        val outputStream = FileOutputStream(myFile)
//
//        try {
//            // Decode the input stream into a bitmap
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//
//            // Compress the bitmap
//            val compressedBitmap = compressBitmap(bitmap, MAXIMAL_SIZE)
//
//            // Write the compressed bitmap to the output stream
//            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            outputStream.close()
//            inputStream.close()
//        }
//
//        return myFile
//    }
//
//
//    private fun compressBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//        var quality = 100
//
//        while (stream.toByteArray().size > maxSize && quality > 0) {
//            stream.reset()
//            quality -= 10
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
//        }
//
//        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().size)
//    }








}