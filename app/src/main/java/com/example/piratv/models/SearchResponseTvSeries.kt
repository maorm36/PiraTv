package com.example.piratv.models

import com.google.gson.annotations.SerializedName

data class SearchResponseTvSeries (

    @SerializedName("seasons")
    val seasons: List<SeasonInfo> = emptyList()

)