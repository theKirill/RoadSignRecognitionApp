package com.yanyushkin.roadsignrecognition.extensions

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.yanyushkin.roadsignrecognition.R
import com.yanyushkin.roadsignrecognition.SNACKBAR_DURATION

@SuppressLint("WrongConstant")
fun AppCompatActivity.showSnackBar(view: View, context: AppCompatActivity, messageId: Int) {
    val snackBar = Snackbar.make(view, messageId, Snackbar.LENGTH_LONG)
    val snackBarView = snackBar.view
    snackBarView.setBackgroundColor(
        ContextCompat.getColor(
            context,
            R.color.colorWarning
        )
    )
    snackBar.apply {
        duration = SNACKBAR_DURATION
        show()
    }
}