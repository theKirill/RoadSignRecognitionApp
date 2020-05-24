package com.yanyushkin.roadsignrecognition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import kotlinx.android.synthetic.main.activity_video_cam.*

class VideoCamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("9533af34-15ed-4e4c-9821-62e46fe931b6")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_video_cam)

       /* mapview.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0), null
        )*/
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart();
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }
}
