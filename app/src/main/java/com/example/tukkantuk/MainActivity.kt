package com.example.tukkantuk

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tukkantuk.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private var currentStories: List<ListStory> = emptyList()
    private lateinit var auth: FirebaseAuth
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestLocationPermission() {
        when {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Location permission is already granted, proceed with functionality that requires this permission
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show an explanation to the user why the permission is needed
                // This can be a dialog or a snackbar
                showRationaleDialog()
            }
            else -> {
                // No explanation needed, request the permission
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                // You can now use location features
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app requires location permission to access certain features.")
            .setPositiveButton("OK") { _, _ ->
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndRequestLocationPermission()


        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        viewModel.getSession().observe(this) { user ->
            if (user.token.isEmpty() && user.token.isBlank()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name) // Provide your channel name here
            val descriptionText = getString(R.string.channel_description) // Provide your channel description here
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }


        viewModel.getStories().observe(this) { story ->
            if (story != null) {
                when (story) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        val newStories = story.data.listStory
                        if (newStories.size > currentStories.size) {
                            val newStory = newStories.first() // Assuming the new story comes first in the list
                            sendNotification(newStory.name, newStory.description)
                        }
                        currentStories = newStories
                        setListStory(currentStories)
                        showLoading(false)
                    }

                    is Result.Error -> {
                        showLoading(false)
                    }

                    else -> {}
                }
            } else {
                showToast(getString(R.string.empty_data))
            }
        }
        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }

        setupView()
        setupToolbar()
    }

    private fun sendNotification(title: String?, message: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSubText(getString(R.string.notification_subtext))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun setupToolbar() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    viewModel.logout()
//                    auth.signOut()
                    showToast(getString(R.string.berhasil_keluar))
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    true

                }

                else -> false
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setListStory(story: List<ListStory>) {
        val adapter = StoryAdapter()
        adapter.submitList(story)
        binding.rvStory.adapter = adapter
        binding.rvStory.visibility = View.VISIBLE
    }
    companion object {
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "New Story"
        const val NOTIFICATION_ID = 1
    }

}