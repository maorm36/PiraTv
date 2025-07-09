package com.example.piratv.models

import com.google.gson.annotations.SerializedName

data class EpisodeInfo(
    @SerializedName("episode_number") val episodeNumber: Int
)