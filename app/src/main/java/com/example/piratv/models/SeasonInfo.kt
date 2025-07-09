package com.example.piratv.models

import com.google.gson.annotations.SerializedName

data class SeasonInfo(

    @SerializedName("episode_count")
    val episodeCount: Int,

    @SerializedName("season_number")
    val seasonNumber: Int
)
