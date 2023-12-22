package com.example.tukkantuk

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String? = null,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody
    ): UploadResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): StoryListResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token : String? = null,
        @Query("location") location : Int = 1,
    ): StoryListResponse




}
