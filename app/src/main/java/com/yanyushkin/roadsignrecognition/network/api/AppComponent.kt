package com.yanyushkin.roadsignrecognition.network.api

import androidx.lifecycle.ViewModel
import com.yanyushkin.roadsignrecognition.di.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {

    fun injectsVM(viewModel: ViewModel)
}