package com.yanyushkin.roadsignrecognition.ui.photo

import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.yanyushkin.roadsignrecognition.IMAGE_PATH_KEY
import com.yanyushkin.roadsignrecognition.R
import com.yanyushkin.roadsignrecognition.domain.RoadSignInfo
import com.yanyushkin.roadsignrecognition.extensions.gone
import com.yanyushkin.roadsignrecognition.extensions.show
import com.yanyushkin.roadsignrecognition.extensions.showSnackBar
import com.yanyushkin.roadsignrecognition.extensions.toMat
import com.yanyushkin.roadsignrecognition.states.ScreenState
import com.yanyushkin.roadsignrecognition.utils.BaseViewModelFactory
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.opencv.android.OpenCVLoader

class PhotoActivity : AppCompatActivity() {

    private lateinit var photoVM: PhotoVM
    private lateinit var sourceBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        photoVM =
            ViewModelProvider(this, BaseViewModelFactory { PhotoVM(this) }).get(PhotoVM::class.java)
        OpenCVLoader.initDebug()
        initViews()
        photoVM.classify(sourceBitmap)
        initObservers()
    }

    private fun initViews() {
        close_btn.setOnClickListener { finish() }

        val path = intent.getParcelableExtra<Uri>(IMAGE_PATH_KEY)

        path?.let {
            getSourceBitmap(path)
            Glide
                .with(this)
                .load(path)
                .into(photo_iv)
        }
    }

    private fun getSourceBitmap(uri: Uri) {
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(uri, "r") as ParcelFileDescriptor
        val fileDescriptor = parcelFileDescriptor.fileDescriptor

        sourceBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
    }

    private fun initObservers() {
        photoVM.state.observe(this, Observer {
            when (it) {
                ScreenState.SUCCESS -> {
                    fillSignInfo(photoVM.signInfo.value!!)
                    all_info_layout.show()
                    progress_layout.gone()
                }
                ScreenState.ERROR_NO_INTERNET -> {
                    showSnackBar(
                        photo_main_layout,
                        this,
                        R.string.no_internet_sb
                    )
                }
                ScreenState.ERROR_OTHER -> {
                    showSnackBar(photo_main_layout, this, R.string.error_sb)
                }
            }
        })
        photoVM.signBitmap.observe(this, Observer {
            photo_sign_iv.setImageBitmap(photoVM.signBitmap.value!!)
        })
    }

    private fun fillSignInfo(sign: RoadSignInfo) {
        sign_name_tv.text = sign.name
        sign_type_tv.text = sign.type
        Glide
            .with(this)
            .load(sign.img)
            .into(sign_image_iv)
        sign_description_tv.text = Html.fromHtml(
            sign.description,
            Html.FROM_HTML_MODE_LEGACY
        )
    }
}
