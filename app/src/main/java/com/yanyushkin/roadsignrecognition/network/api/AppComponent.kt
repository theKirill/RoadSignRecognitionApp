package com.yanyushkin.roadsignrecognition.network.api

import androidx.lifecycle.ViewModel
import com.yanyushkin.roadsignrecognition.classifier.Analyzer
import com.yanyushkin.roadsignrecognition.di.NetworkModule
import com.yanyushkin.roadsignrecognition.ui.photo.PhotoVM
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {

    fun injectsVM(viewModel: ViewModel)
    fun injectsPhotoVM(photoVM: PhotoVM)
    fun injectsAnalyzer(analyzer: Analyzer)
}