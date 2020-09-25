package com.axellaffite.fastgallery

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class ImageViewPager<Image>(var images: List<Image>, private val converter: (Image) -> Uri) :
    RecyclerView.Adapter<ImageViewPager.ImageViewHolder>() {

    class ImageViewHolder(val view: SubsamplingScaleImageView, val target: Target): RecyclerView.ViewHolder(view)

    var onImageLoadListener: OnImageLoadListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_displayer_view_holder, parent, false) as SubsamplingScaleImageView

        return ImageViewHolder(view, object: Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let {
                    val res = onImageLoadListener?.onSuccess(bitmap) ?: bitmap
                    view.setImage(ImageSource.bitmap(res))
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                e?.printStackTrace(System.err)
                onImageLoadListener?.onError()?.let {
                    view.setImage(ImageSource.bitmap(it))
                }
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                onImageLoadListener?.onPrepare()?.let {
                    view.setImage(ImageSource.bitmap(it))
                }
            }

        })
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Picasso
            .get()
            .load(converter(images[position]))
            .into(holder.target)
    }

    override fun getItemCount() = images.size
}