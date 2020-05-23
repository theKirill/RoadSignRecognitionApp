package com.yanyushkin.roadsignrecognition.network.responses

interface BaseResponse<T> {

    fun transform(): T
}