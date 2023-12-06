package com.example.mystory

import androidx.lifecycle.ViewModel
import java.io.File

class ViewModelUpload(private val repository: Repository) : ViewModel() {

    fun uploadImage(file: File, description: String) = repository.uploadImage(file, description)
}