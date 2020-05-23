package com.yanyushkin.roadsignrecognition.di

import com.yanyushkin.roadsignrecognition.BASE_URL
import com.yanyushkin.roadsignrecognition.BuildConfig
import com.yanyushkin.roadsignrecognition.network.AuthInterceptor
import com.yanyushkin.roadsignrecognition.network.api.RoadSignInfoApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            with(okHttpClientBuilder) {
                addInterceptor(interceptor)
                addInterceptor(AuthInterceptor())
            }
        }
        return okHttpClientBuilder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideRoadSignInfoApi(retrofit: Retrofit): RoadSignInfoApi =
        retrofit.create(RoadSignInfoApi::class.java)
}