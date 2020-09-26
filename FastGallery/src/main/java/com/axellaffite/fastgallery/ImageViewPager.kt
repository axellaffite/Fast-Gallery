package com.axellaffite.fastgallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class ImageViewPager<Image>(var images: List<Image>, private val converter: (Image, ImageLoader) -> Unit) :
    RecyclerView.Adapter<ImageViewPager.ImageViewHolder>() {

    class ImageViewHolder(val view: SubsamplingScaleImageView): RecyclerView.ViewHolder(view)

    var onImageLoadListener: OnImageLoadListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_displayer_view_holder, parent, false) as SubsamplingScaleImageView

        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        onImageLoadListener?.onPrepare()
        converter(images[position], ImageLoader(holder.view.context, holder.view, onImageLoadListener))
    }

    override fun getItemCount() = images.size
}