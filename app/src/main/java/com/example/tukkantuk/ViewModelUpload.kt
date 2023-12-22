package com.example.tukkantuk

import androidx.lifecycle.ViewModel
import com.example.tukkantuk.Repository
import java.io.File

class ViewModelUpload(private val repository: Repository) : ViewModel() {

    fun uploadStory(file: File, description: String, latitude: Double?, longitude: Double?) = repository.uploadStory(file, description, latitude, longitude)
}