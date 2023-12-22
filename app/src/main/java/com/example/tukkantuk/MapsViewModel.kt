package com.example.tukkantuk

import androidx.lifecycle.ViewModel

class MapsViewModel(private val repository: Repository) : ViewModel() {
    //    val markers: LiveData<StoryResponse> = repository.listResponse
    fun getStoriesWithLocation() = repository.getStoriesWithLocation()
}