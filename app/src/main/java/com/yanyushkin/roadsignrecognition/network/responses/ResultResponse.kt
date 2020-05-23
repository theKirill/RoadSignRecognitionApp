package com.yanyushkin.roadsignrecognition.network.responses

import com.google.gson.annotations.SerializedName
import com.yanyushkin.roadsignrecognition.domain.RoadSignInfo

data class ResultResponse(
    @SerializedName("result")
    val result: RoadSignInfoResponse?
)