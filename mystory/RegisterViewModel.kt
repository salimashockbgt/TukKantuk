package com.example.mystory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel(private val repository: Repository) : ViewModel() {
    fun register(
        name: String, email: String, password: String
    ): LiveData<ResultState<RegisterResponse>> = repository.register(name, email, password)
}