package com.yanyushkin.roadsignrecognition.network.api

import com.yanyushkin.roadsignrecognition.di.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {
}