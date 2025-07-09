package com.example.piratv.models

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("results")
    val results: List<TitleResult> = emptyList()
)