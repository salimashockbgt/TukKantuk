package com.example.mystory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class Repository private constructor(
    private val userPreference: Preference,
    private val apiService: ApiService
) {

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<ResultState<RegisterResponse>> = liveData {
        emit(ResultState.Loading)

        try {
            val client = apiService.register(name, email, password)
            emit(ResultState.Success(client))
        } catch (e: Exception) {
            Log.e("SignupViewModel", "register: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }

    }

    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val client = apiService.login(email, password)
            emit(ResultState.Success(client))

        } catch (e: Exception) {
            Log.e("LoginViewModel", "login: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getAllStories(): LiveData<ResultState<StoryListResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val token: String = runBlocking { userPreference.getSession().first().token }
            val client = apiService.getAllStories("Bearer $token")
            emit(ResultState.Success(client))
        } catch (e: Exception) {
            Log.e("MainViewModel", "getAllStories: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun uploadImage(
        file: File, description: String
    ): LiveData<ResultState<UploadResponse>> = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val token: String = runBlocking { userPreference.getSession().first().token }
        try {
            val client = apiService.uploadStory("Bearer $token", multipartBody, requestBody)
            emit(ResultState.Success(client))
        } catch (e: Exception) {
            Log.e("UploadViewModel", "uploadStories: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: Preference, apiService: ApiService
        ): Repository = instance ?: synchronized(this) {
            instance ?: Repository(userPreference, apiService)
        }.also { instance = it }
    }
}
