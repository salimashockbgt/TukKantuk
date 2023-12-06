package com.example.mystory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
        fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
        }

        fun logout() {
        viewModelScope.launch {
        repository.logout()
        }
        }

        fun getAllStories(): LiveData<ResultState<StoryListResponse>> = repository.getAllStories()

        }