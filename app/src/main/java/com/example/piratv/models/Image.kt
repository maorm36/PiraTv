package com.example.piratv.models

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)
