package com.example.tukkantuk

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tukkantuk.LoginResponse
import com.example.tukkantuk.Repository
import com.example.tukkantuk.Result
import com.example.tukkantuk.UserModel
import kotlinx.coroutines.launch

class ViewModelLogin(private val repository: Repository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>>
            = repository.login(email, password)

}
