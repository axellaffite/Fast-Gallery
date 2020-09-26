package com.axellaffite.fastgallery

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ImageLoader(val context: Context, private val targetImage: SubsamplingScaleImageView, private val onImageLoadListener: OnImageLoadListener?) {

    companion object {
        private val downloadMutex = Mutex()
        private val urlMap = mapOf<String, Bitmap>()
        private val fileMap = mapOf<String, File>()
    }
    private var file: File? = null

    init {
        targetImage.setOnImageEventListener(object: SubsamplingScaleImageView.DefaultOnImageEventListener() {
            override fun onImageLoadError(e: Exception?) {
                onImageLoadListener?.onError()
            }

            override fun onImageLoaded() {
                onImageLoadListener?.onSuccess()
            }
        })
    }

    /**
     * Download the image from the given url.
     *
     * If the cacheInPhoneMemory is set to true, this function
     * will try to save the file into a temp file to allow the
     * sub-sampling optimization.
     *
     * If the phone memory is too low, the image is not cached and
     * the optimization cannot be done.
     *
     * @param url The image URL.
     * @param cacheInPhoneMemory Try to cache the file in the phone's memory
     * by default.
     */
    suspend fun fromURL(url: String, cacheInPhoneMemory: Boolean = true) {
        try {
            val cached = getCachedFile(url)
            if (cached.exists()) {
                Log.d(this::class.simpleName, "File is cached, loading from it")
                fromFile(cached)
            } else {
                downloadMutex.lock()
                Log.d(this::class.simpleName, "Downloading $url")
                val image = withContext(IO) {
                    Picasso.get().load(Uri.parse(url)).get()
                }
                downloadMutex.unlock()

                fromBitmap(image, cacheInPhoneMemory)
            }
        } catch (e: IOException) {
            cleanCachedFile()
            onImageLoadListener?.onError()
        }
    }

    private fun getCachedFile(url: String): File {
        return File(context.cacheDir, "$url.bmp")
    }


    /**
     * Set the image from a Bitmap.
     *
     * If the cacheInPhoneMemory is set to true, this function
     * will try to save the file into a temp file to allow the
     * sub-sampling optimization.
     *
     * If the phone memory is too low, the image is not cached and
     * the optimization cannot be done.
     *
     * @param image The bitmap
     * @param cacheInPhoneMemory Try to cache the file in the phone's memory
     * by default.
     */
    @SuppressLint("SimpleDateFormat")
    suspend fun fromBitmap(image: Bitmap, cacheInPhoneMemory: Boolean = true) {
        if (cacheInPhoneMemory) {
            try {
                val prefix = SimpleDateFormat("yyyy/mm/dd_HH:mm:ss${Random().nextLong()}").format(Date())

                cleanCachedFile()
                file = withContext(IO) {
                    File.createTempFile(prefix, ".bmp", context.cacheDir)
                }.also {
                    fromFile(it)
                }
            } catch (e: IOException) {
                cleanCachedFile()
                fromBitmap(image, false)
            }
        } else {
            targetImage.setImage(ImageSource.bitmap(image))
        }
    }

    /**
     * Best option to use, the optimization is used by default with this option.
     *
     * @param file
     */
    @Throws(IOException::class)
    fun fromFile(file: File) {
        targetImage.setImage(ImageSource.uri(Uri.parse(file.absolutePath)))
    }

    /**
     * Set the image from a resource.
     *
     * @param resID
     */
    fun fromResource(resID: Int) {
        targetImage.setImage(ImageSource.resource(resID))
    }

    /**
     * Do not call this function. It's called by the pager.
     *
     */
    fun cleanCachedFile() = file?.delete()

}