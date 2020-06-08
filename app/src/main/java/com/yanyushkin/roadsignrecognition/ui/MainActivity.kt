package com.yanyushkin.roadsignrecognition.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yanyushkin.roadsignrecognition.R
import com.yanyushkin.roadsignrecognition.extensions.showSnackBar
import com.yanyushkin.roadsignrecognition.ui.video.VideoCamActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setClickListenersForButtons()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION && checkGrantResults(grantResults))
            startActivity(Intent(this, PhotoCamActivity::class.java))
        else
            showSnackBar(main_layout, this, R.string.request_camera_permission_sb)
    }

    private fun setClickListenersForButtons() {
        photocam_btn.setOnClickListener {
            if (hasAllPermissions()) {
                startActivity(Intent(this, PhotoCamActivity::class.java))
            } else {
                if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[0]))
                    showSnackBar(main_layout, this, R.string.request_camera_permission_sb)

                requestPermissions()
            }
        }

        videocam_btn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    VideoCamActivity::class.java
                )
            )
        }
    }

    private fun hasAllPermissions(): Boolean =
        REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
        }

    private fun requestPermissions() =
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSION
        )

    private fun checkGrantResults(grantResults: IntArray): Boolean =
        grantResults.isNotEmpty() && grantResults.all { res -> res == PERMISSION_GRANTED }

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
        const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
