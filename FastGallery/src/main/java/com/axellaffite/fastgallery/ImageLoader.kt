package com.axellaffite.fastgallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

@Suppress("BlockingMethodInNonBlockingContext")
class ImageLoader<Image>(val context: Context, private val targetImage: SubsamplingScaleImageView, private val onImageLoadListener: OnImageLoadListener<Image>?) {

    private var lastImage: Any? = null

    companion object {
        private val cacheMutex = Mutex()
        private val fileMap = mutableMapOf<String, Uri>()

        fun cleanCache() {
            Log.d(this::class.simpleName, "Cleaning up cache")
            val files = fileMap.keys.toList()
            fileMap.clear()

            files.forEach {
                File(it).delete()
            }
        }
    }

    init {
        targetImage.setOnImageEventListener(object: SubsamplingScaleImageView.DefaultOnImageEventListener() {
            override fun onImageLoadError(e: Exception?) {
                onImageLoadListener?.onError(e)
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
    suspend fun fromURL(url: String, cacheInPhoneMemory: Boolean = true) = withContext(IO) {
        try {
            val cached = getCachedFile(url)
            Log.d(this@ImageLoader::class.simpleName, "File: $cached, exists: ${cached?.exists()}")
            if (cached?.exists() == true && cached.canRead()) {
                fromFile(fileMap[url]!!)
            } else {
                fromBitmap(Bitmap.createBitmap(1,1,Bitmap.Config.RGB_565))
                val imageStream = withContext(IO) {
                    Log.d(this@ImageLoader::class.simpleName, "Donwloading $url")

                    OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .build()
                        .run {
                            val req = Request.Builder().get().url(url).build()
                            newCall(req).execute().body()?.byteStream()
                        }
                }


                if (cacheInPhoneMemory) {
                    try {
                        cacheInMemory(url, imageStream)
                        fromFile(fileMap[url]!!)
                    } catch (e: IOException) {
                        Log.e(this::class.simpleName, "Unable to cache the file")

                        val image = BitmapFactory.decodeStream(imageStream)
                        image?.also { fromBitmap(it) }
                            ?: run { onImageLoadListener?.onError(e) }
                    }
                } else {
                    val image = BitmapFactory.decodeStream(imageStream)
                    fromBitmap(image)
                }
            }
        } catch (e: IOException) {
            onImageLoadListener?.onError(e)
        }
    }

    private fun getCachedFile(url: String): File? {
        return fileMap[url]?.toFile()
    }

    @Throws(IOException::class)
    private suspend fun cacheInMemory(url: String, bitmap: InputStream?): File = withContext(IO) {
        val filename = toMD5(url)
        context.cacheDir.mkdirs()
        val file = File(context.filesDir, "$filename.jpg")
        bitmap?.copyTo(file.outputStream())

        Log.d(this@ImageLoader::class.simpleName, "Cached to: ${file.absolutePath}")

        file.also {
            fileMap[url] = Uri.fromFile(it)
        }
    }

    @Throws(IOException::class)
    private suspend fun cacheInMemory(url: String, bitmap: Bitmap): File = withContext(IO) {
        cacheMutex.withLock {
            val filename = toMD5(url)
            context.cacheDir.mkdirs()
            val file = File(context.filesDir, "$filename.jpg")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, file.outputStream())

            Log.d(this@ImageLoader::class.simpleName, "Cached to: ${file.absolutePath}")

            file.also {
                fileMap[url] = Uri.fromFile(it)
            }
        }
    }

    private fun toMD5(str: String) :String {
        return MessageDigest.getInstance("MD5")
            .digest(str.toByteArray()).joinToString("") { "%02x".format(it) }
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
    suspend fun fromBitmap(image: Bitmap) = withContext(Main) {
        targetImage.setImage(ImageSource.bitmap(image))
    }

    /**
     * Best option to use, the optimization is used by default with this option.
     *
     * @param uri
     */
    @Throws(IOException::class)
    suspend fun fromFile(uri: Uri) = withContext(Main) {
        if (lastImage != uri) {
            lastImage = uri
            targetImage.setImage(ImageSource.uri(uri))
        }
    }

    /**
     * Set the image from a resource.
     *
     * @param resID
     */
    suspend fun fromResource(resID: Int) = withContext(Main) {
        targetImage.setImage(ImageSource.resource(resID))
    }

}