package com.yanyushkin.roadsignrecognition.network.responses

import com.google.gson.annotations.SerializedName

data class RoadSignInfoResponse (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("type")
    val type: String
)