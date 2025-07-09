package com.example.piratv.models

import com.google.gson.annotations.SerializedName

data class EpisodesMetadata(
    @SerializedName("episodes") val episodes: List<EpisodeInfo>
)