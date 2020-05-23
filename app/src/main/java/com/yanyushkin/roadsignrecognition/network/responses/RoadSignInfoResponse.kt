package com.yanyushkin.roadsignrecognition.network.responses

import com.google.gson.annotations.SerializedName
import com.yanyushkin.roadsignrecognition.domain.RoadSignInfo

data class RoadSignInfoResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("important_info")
    val importantInfo: String?,
    @SerializedName("img_template_url")
    val img: String?
) : BaseResponse<RoadSignInfo> {
    override fun transform(): RoadSignInfo = RoadSignInfo(
        id ?: 0,
        name ?: "",
        description ?: "",
        type ?: "",
        importantInfo ?: "",
        img ?: ""
    )
}