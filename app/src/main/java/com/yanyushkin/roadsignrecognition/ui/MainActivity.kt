package com.yanyushkin.roadsignrecognition.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yanyushkin.roadsignrecognition.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setClickListenersForButtons()
    }

    private fun setClickListenersForButtons() {
        photocam_btn.setOnClickListener{ }

        videocam_btn.setOnClickListener{ }
    }
}
