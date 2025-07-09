package com.example.piratv.models

import com.google.gson.annotations.SerializedName

data class TvMetadata(
    @SerializedName("seasons") val seasons: List<SeasonInfo>
)