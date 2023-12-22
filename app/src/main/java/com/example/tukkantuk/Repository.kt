package com.example.tukkantuk

import android.content.Context
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
    fun getStoriesWithLocation(): LiveData<Result<StoryListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token: String = runBlocking { userPreference.getSession().first().token }
            val client = apiService.getStoriesWithLocation("Bearer $token")
            emit(Result.Success(client))
        } catch (e: Exception) {
            Log.e("MainViewModel", "getStoriesWithLocation: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)

        try {
            val client = apiService.register(name, email, password)
            emit(Result.Success(client))
        } catch (e: Exception) {
            Log.e("RegisterViewModel", "register: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }

    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.login(email, password)
            emit(Result.Success(client))

        } catch (e: Exception) {
            Log.e("ViewModelLogin", "login: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStories(): LiveData<Result<StoryListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token: String = runBlocking { userPreference.getSession().first().token }
            val client = apiService.getStories("Bearer $token")
            emit(Result.Success(client))
        } catch (e: Exception) {
            Log.e("MainViewModel", "getAllStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun uploadStory(
        file: File, description: String, latitude: Double?, longitude: Double?
    ): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)
        val reqImage = file.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            reqImage
        )

        val validLatitude = latitude ?: DEFAULT_LATITUDE
        val validLongitude = longitude ?: DEFAULT_LONGITUDE
        val reqBodyDescription = description.toRequestBody("text/plain".toMediaType())
        val reqBodyLatitude = validLatitude.toString().toRequestBody("text/plain".toMediaType())
        val reqBodyLongitude = validLongitude.toString().toRequestBody("text/plain".toMediaType())
        val token: String = runBlocking { userPreference.getSession().first().token }

        try {
            val client = apiService.uploadStory("Bearer $token", multipartBody, reqBodyDescription, reqBodyLatitude, reqBodyLongitude)
            emit(Result.Success(client))
        } catch (e: Exception) {
            Log.e("ViewModelUpload", "uploadStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
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
        private const val DEFAULT_LATITUDE = -6.880574068069173
        private const val DEFAULT_LONGITUDE = 107.60417083841581

    }
}
object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = Preference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(pref, apiService)
    }
}
