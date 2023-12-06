package com.example.mystory
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadResponse(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
) : Parcelable
