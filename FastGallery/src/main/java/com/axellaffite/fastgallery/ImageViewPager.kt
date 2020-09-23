package com.axellaffite.fastgallery

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class ImageViewPager<Image>(var images: List<Image>, private val converter: (Image) -> Uri) :
    RecyclerView.Adapter<ImageViewPager.ImageViewHolder>() {

    class ImageViewHolder(val view: View): RecyclerView.ViewHolder(view)

    var onImageLoadListener: OnImageLoadListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_displayer_view_holder, parent, false)

        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val container = holder.view
        if (container is SubsamplingScaleImageView) {
            Picasso.get().load(converter(images[position])).into(object: Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    bitmap?.let {
                        val res = onImageLoadListener?.onSuccess(bitmap) ?: bitmap
                        container.setImage(ImageSource.bitmap(res))
                    }
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    e?.printStackTrace(System.err)
                    onImageLoadListener?.onError()?.let {
                        container.setImage(ImageSource.bitmap(it))
                    }
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    onImageLoadListener?.onPrepare()?.let {
                        container.setImage(ImageSource.bitmap(it))
                    }
                }

            })
        }
    }

    override fun getItemCount() = images.size
}