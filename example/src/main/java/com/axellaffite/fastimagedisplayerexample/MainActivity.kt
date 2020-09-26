package com.axellaffite.fastimagedisplayerexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.axellaffite.fastgallery.FastGallery
import com.axellaffite.fastgallery.ImageLoader
import com.axellaffite.fastgallery.slider_animations.SlideAnimations
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val images = listOf (
        "https://images.unsplash.com/photo-1600656862818-db82cdb7d409?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
        "https://images.unsplash.com/photo-1600758946933-422348d8b5d4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
        "https://images.unsplash.com/photo-1427847907429-d1ba99bf013d?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1920&fit=max&ixid=eyJhcHBfaWQiOjF9",
        "https://upload.wikimedia.org/wikipedia/commons/c/cc/ESC_large_ISS022_ISS022-E-11387-edit_01.JPG"
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
            .withOffscreenLimit(3)
            .withConverter { imgSource: String, loader: ImageLoader ->
                lifecycleScope.launchWhenResumed {
                    loader.fromURL(imgSource, false)
                }
            }.build().show(supportFragmentManager, "theTagYouWant")
    }
}