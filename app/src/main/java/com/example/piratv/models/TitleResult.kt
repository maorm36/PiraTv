package com.example.piratv.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TitleResult(
    @SerializedName("id")
    val id: String,

    // "tv" or "movie"
    @SerializedName("media_type")
    val type: String,

    // if tv: name, else empty
    @SerializedName("name")
    val name: String,

    @SerializedName("original_name")
    val originalName: String,

    // if movie: title, else empty
    @SerializedName("title")
    val title: String,

    @SerializedName("original_title")
    val originalTitle: String,

    @SerializedName("poster_path")
    val primaryImage: String,

    @SerializedName("first_air_date")
    val date: String,

    @SerializedName("adult")
    val isAdult: Boolean,

    @SerializedName("overview")
    val overview: String
) : Parcelable
