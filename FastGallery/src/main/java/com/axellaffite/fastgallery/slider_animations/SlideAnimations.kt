package com.axellaffite.fastgallery.slider_animations

import android.os.Build
import androidx.annotation.RequiresApi
import com.axellaffite.fastgallery.slider_animations.animations.DepthPageTransformer
import com.axellaffite.fastgallery.slider_animations.animations.ZoomOutPageTransformer

class SlideAnimations {
    companion object {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun depthPageAnimation() = DepthPageTransformer()

        fun zoomOutAnimation() = ZoomOutPageTransformer()
    }
}