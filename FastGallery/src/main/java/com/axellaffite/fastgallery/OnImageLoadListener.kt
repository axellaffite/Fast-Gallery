package com.axellaffite.fastgallery

import android.graphics.Bitmap

abstract class OnImageLoadListener {
    fun onError() { }

    fun onSuccess() { }

    fun onPrepare() { }
}