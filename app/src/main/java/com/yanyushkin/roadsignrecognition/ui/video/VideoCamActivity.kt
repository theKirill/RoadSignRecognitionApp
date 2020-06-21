package com.yanyushkin.roadsignrecognition.ui.video

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import com.yanyushkin.roadsignrecognition.R
import com.yanyushkin.roadsignrecognition.classifier.Analyzer
import kotlinx.android.synthetic.main.activity_video_cam.*
import org.opencv.android.OpenCVLoader
import java.util.*
import java.util.concurrent.Executors

class VideoCamActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imagePreview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var cameraControl: CameraControl
    private val executor = Executors.newSingleThreadExecutor()
    private var myLocation: PlacemarkMapObject? = null
    private lateinit var analyzer: Analyzer
    private var linearZoom = 0f
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_cam)
        OpenCVLoader.initDebug()

        tts = TextToSpeech(this, this)
        analyzer = Analyzer(this)
        video_preview_view.post { startCamera() }
        val locationManager = getSystemService(Context.LOCATION_SERVICE)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onInit(p0: Int) {
        if (p0 == TextToSpeech.SUCCESS)
            tts!!.setLanguage(Locale.forLanguageTag("ru"))
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

    /**
     * Инициализация камеры
     */
    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        initUseCases()

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera =
                cameraProvider.bindToLifecycle(this, cameraSelector, imagePreview, imageAnalysis)
            cameraControl = camera.cameraControl
            video_preview_view.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreview.setSurfaceProvider(video_preview_view.createSurfaceProvider(camera.cameraInfo))
        }, ContextCompat.getMainExecutor(this))
    }

    private fun initUseCases() {
        imagePreview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
        imageAnalysis.setAnalyzer(executor, analyzer)
        initObservers()
    }

    /**
     * Инициализация наблюдателей
     */
    private fun initObservers() {
        analyzer.signURL.observe(this, Observer {
            Glide
                .with(this)
                .load(analyzer.signURL.value)
                .into(photo_sign_iv)
            //photo2_iv.setImageBitmap(analyzer.signBitmap.value)
        })
        analyzer.stateSignInfo.observe(this, Observer {
            val text = analyzer.stateSignInfo.value.toString().split('\n')
            Toast.makeText(this, text[0], Toast.LENGTH_LONG).show()
            speak(text[1])
        })
    }

    /**
     * Голосовое предупреждение (не для всех знаков, если есть важная информация)
     */
    private fun speak(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}
