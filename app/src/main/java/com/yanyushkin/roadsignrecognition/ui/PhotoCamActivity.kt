package com.yanyushkin.roadsignrecognition.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.util.concurrent.ListenableFuture
import com.yanyushkin.roadsignrecognition.R
import kotlinx.android.synthetic.main.activity_photo_cam.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class PhotoCamActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imagePreview: Preview
    private lateinit var cameraControl: CameraControl
    private lateinit var imageCapture: ImageCapture
    private val executor = Executors.newSingleThreadExecutor()
    private var linearZoom = 0f
    private lateinit var kek: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_cam)

        preview_view.post { startCamera() }
        make_photo_btn.setOnClickListener {
            takePicture()
        }
        kek = BottomSheetBehavior.from(bottomsheet)
        kek.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (linearZoom <= 0.9) {
                    linearZoom += 0.1f
                }
                cameraControl.setLinearZoom(linearZoom)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (linearZoom >= 0.1) {
                    linearZoom -= 0.1f
                }
                cameraControl.setLinearZoom(linearZoom)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        initUseCases()

        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, imagePreview, imageCapture)
            cameraControl = camera.cameraControl
            preview_view.preferredImplementationMode = PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreview.setSurfaceProvider(preview_view.createSurfaceProvider(camera.cameraInfo))
        }, ContextCompat.getMainExecutor(this))
    }

    private fun initUseCases() {
        imagePreview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
        imageCapture = ImageCapture.Builder()
            .setTargetResolution(Size(720, 1280))
            .setTargetRotation(preview_view.display.rotation)
            .build()
    }

    private fun takePicture() {
        val file = File(externalMediaDirs.first(), SimpleDateFormat(FILENAME, Locale.US)
            .format(System.currentTimeMillis()) + PHOTO_EXTENSION)

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(outputFileOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val msg = "Photo capture succeeded: ${file.absolutePath}"
                preview_view.post {
                    Toast.makeText(this@PhotoCamActivity, msg, Toast.LENGTH_LONG).show()
                    kek.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onError(exception: ImageCaptureException) {
                val msg = "Photo capture failed: ${exception.message}"
                preview_view.post {
                    Toast.makeText(this@PhotoCamActivity, msg, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setupBottomSheet() {

    }

    companion object {
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
    }
}
