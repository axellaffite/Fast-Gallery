package com.axellaffite.fastgallery

import android.graphics.Bitmap

abstract class OnImageLoadListener {
    fun onError(): Bitmap? { return null }

    fun onSuccess(bitmap: Bitmap): Bitmap = bitmap

    fun onPrepare(): Bitmap? = null
}