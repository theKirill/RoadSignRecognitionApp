package com.yanyushkin.roadsignrecognition.network

import com.yanyushkin.roadsignrecognition.network.api.RoadSignInfoApi
import com.yanyushkin.roadsignrecognition.sendRequestInBackground
import javax.inject.Inject

class RoadSignInfoRepository @Inject constructor(private val roadSignInfoApi: RoadSignInfoApi) {

    fun getSignInfo(id: Int) = roadSignInfoApi.getSignInfo(id).sendRequestInBackground()
}