package com.example.mystory
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryListResponse(
    @field:SerializedName("error") val error: Boolean,

    @field:SerializedName("message") val message: String,

    @field:SerializedName("listStory") val listStory: List<ListStory>
) : Parcelable

@Parcelize
data class ListStory(
    @field: SerializedName("id") val id: String? = null,

    @field: SerializedName("name") val name: String? = null,

    @field: SerializedName("description") val description: String? = null,

    @field: SerializedName("photoUrl") val photoUrl: String? = null,

    @field: SerializedName("createdAt") val createdAt: String? = null,

    @field:SerializedName("lon") val lon: Double? = null,

    @field:SerializedName("lat") val lat: Double? = null
) : Parcelable