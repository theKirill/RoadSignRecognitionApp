package com.yanyushkin.roadsignrecognition.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

/**
 * Проверка необходимых разрешений для работы программы
 */
class PermissionsHelper(
    private val context: AppCompatActivity,
    private val REQUIRED_PERMISSIONS: Array<String>
) {

    fun checkAllPermissions(onHaveAllPermissions: () -> Unit, message: () -> Unit) {
        val haveAllPermissions = REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
        }

        if (haveAllPermissions) {
            onHaveAllPermissions.invoke()
        } else {
            if (shouldShowRequestPermissionRationale(context, REQUIRED_PERMISSIONS[0]))
                message.invoke()

            requestPermissions()
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onHaveAllPermissions: () -> Unit,
        noPermissions: () -> Unit
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION && checkGrantResults(grantResults))
            onHaveAllPermissions.invoke()
        else
            noPermissions.invoke()
    }

    private fun checkGrantResults(grantResults: IntArray): Boolean =
        grantResults.isNotEmpty() && grantResults.all { res -> res == PERMISSION_GRANTED }

    private fun requestPermissions() =
        ActivityCompat.requestPermissions(
            context,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSION
        )

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
        const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
        //val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}