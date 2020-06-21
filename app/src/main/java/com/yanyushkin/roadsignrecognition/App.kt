package com.yanyushkin.roadsignrecognition

import android.app.Application
import com.yanyushkin.roadsignrecognition.di.NetworkModule
import com.yanyushkin.roadsignrecognition.di.AppComponent
import com.yanyushkin.roadsignrecognition.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder()
            .networkModule(NetworkModule())
            .build()
    }
}