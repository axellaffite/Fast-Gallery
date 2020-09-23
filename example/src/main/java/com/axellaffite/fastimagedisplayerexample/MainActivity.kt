package com.axellaffite.fastimagedisplayerexample

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.axellaffite.fastgallery.FastGallery
import com.axellaffite.fastgallery.slider_animations.SlideAnimations
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val images = listOf (
        "https://images.unsplash.com/photo-1600656862818-db82cdb7d409?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
        "https://images.unsplash.com/photo-1600758946933-422348d8b5d4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
        "https://images.unsplash.com/photo-1427847907429-d1ba99bf013d?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1920&fit=max&ixid=eyJhcHBfaWQiOjF9"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            showImages()
        }
    }

    private fun showImages() {
        FastGallery.Builder<String>()
            .withImages(images)
            .withInitialPosition(0)
            .withSlideAnimation(SlideAnimations.depthPageAnimation())
            .withBackgroundResource(R.color.colorGalleryBackground)
            .withOffscreenLimit(2)
            .withConverter {
                Uri.parse(it)
            }.build().show(supportFragmentManager, "theTagYouWant")
    }
}