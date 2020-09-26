package com.axellaffite.fastgallery

abstract class OnImageLoadListener<Image> {
    open fun onError(e: Exception?) { e?.printStackTrace() }

    open fun onSuccess() { }

    open fun onPrepare() { }
}