package com.example.tukkantuk

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tukkantuk.RegisterResponse
import com.example.tukkantuk.Repository
import com.example.tukkantuk.Result

class ViewModelRegister(private val repository: Repository) : ViewModel()  {
    fun register(
        name: String, email: String, password: String
    ): LiveData<Result<RegisterResponse>> = repository.register(name, email, password)
}