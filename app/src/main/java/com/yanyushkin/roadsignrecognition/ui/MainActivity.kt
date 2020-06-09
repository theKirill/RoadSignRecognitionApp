package com.yanyushkin.roadsignrecognition.ui

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yanyushkin.roadsignrecognition.R
import com.yanyushkin.roadsignrecognition.extensions.showSnackBar
import com.yanyushkin.roadsignrecognition.ui.video.VideoCamActivity
import com.yanyushkin.roadsignrecognition.utils.PermissionsHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val REQUIRED_PERMISSIONS =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var permissionsHelper: PermissionsHelper
    private var photo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionsHelper = PermissionsHelper(this, REQUIRED_PERMISSIONS)
        setClickListenersForButtons()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsHelper.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            ::startActivity,
            ::noPermissions
        )
    }

    private fun setClickListenersForButtons() {
        photocam_btn.setOnClickListener {
            photo = true
            permissionsHelper.checkAllPermissions(::startActivity, ::noPermissions)
        }

        videocam_btn.setOnClickListener {
            photo = false
            permissionsHelper.checkAllPermissions(::startActivity, ::noPermissions)
        }
    }

    private fun startActivity() {
        val intent = if (photo)
            Intent(this, PhotoCamActivity::class.java)
        else Intent(
            this,
            VideoCamActivity::class.java
        )

        startActivity(intent)
    }

    private fun noPermissions() =
        showSnackBar(main_layout, this, R.string.request_camera_permission_sb)
}
