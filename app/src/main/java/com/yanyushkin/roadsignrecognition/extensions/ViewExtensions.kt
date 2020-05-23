package com.yanyushkin.roadsignrecognition.extensions

import android.view.View
import androidx.core.view.isVisible

fun View.show() {
    isVisible = true
}

fun View.hide() {
    isVisible = false
}

fun View.gone() {
    visibility = View.GONE
}