package com.yanyushkin.roadsignrecognition.ui.photo

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.yanyushkin.roadsignrecognition.IMAGE_PATH_KEY
import kotlinx.android.synthetic.main.activity_photo.*
import com.yanyushkin.roadsignrecognition.R
import com.yanyushkin.roadsignrecognition.domain.RoadSignInfo
import com.yanyushkin.roadsignrecognition.extensions.gone
import com.yanyushkin.roadsignrecognition.extensions.show
import com.yanyushkin.roadsignrecognition.extensions.showSnackBar
import com.yanyushkin.roadsignrecognition.states.ScreenState
import com.yanyushkin.roadsignrecognition.utils.BaseViewModelFactory
import kotlinx.android.synthetic.main.activity_photo.bottom_sheet_layout
import kotlinx.android.synthetic.main.bottom_sheet.*

class PhotoActivity : AppCompatActivity() {

    private lateinit var photoVM: PhotoVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        photoVM =
            ViewModelProvider(this, BaseViewModelFactory { PhotoVM() }).get(PhotoVM::class.java)
        photoVM.getSignInfo(0)

        initViews()
        initObservers()
    }

    private fun initViews() {
        close_btn.setOnClickListener { finish() }

        val path = intent.getParcelableExtra<Uri>(IMAGE_PATH_KEY)
        Glide
            .with(this)
            .load(path)
            .into(photo_iv)
    }

    private fun initObservers() {
        photoVM.state.observe(this, Observer {
            when (it) {
                ScreenState.SUCCESS -> {
                    fillSignInfo(photoVM.sign.value!!)
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
