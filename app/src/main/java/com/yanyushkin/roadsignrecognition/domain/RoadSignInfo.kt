package com.yanyushkin.roadsignrecognition.domain

data class RoadSignInfo (
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
    val importantInfo: String,
    val img: String
)