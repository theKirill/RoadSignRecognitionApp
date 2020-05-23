package com.yanyushkin.roadsignrecognition.network.api

import com.yanyushkin.roadsignrecognition.network.responses.ResultResponse
import com.yanyushkin.roadsignrecognition.network.responses.RoadSignInfoResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface RoadSignInfoApi {

    @GET("api/signs/{id}")
    fun getSignInfo(@Path("id") id: Int): Observable<ResultResponse>
}