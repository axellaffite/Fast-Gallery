package com.axellaffite.fastgallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class ImageViewPager<Image>(var images: List<Image>, private val converter: (Image, ImageLoader<Image>) -> Unit) :
    RecyclerView.Adapter<ImageViewPager.ImageViewHolder<Image>>() {

    class ImageViewHolder<Image>(val view: SubsamplingScaleImageView, var loader: ImageLoader<Image>? = null): RecyclerView.ViewHolder(view)

    var onImageLoadListener: OnImageLoadListener<Image>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder<Image> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_displayer_view_holder, parent, false) as SubsamplingScaleImageView


        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder<Image>, position: Int) {
        onImageLoadListener?.onPrepare()
        holder.loader = holder.loader ?: ImageLoader(holder.view.context, holder.view, onImageLoadListener)
        converter(images[position], holder.loader!!)
    }

    override fun getItemCount() = images.size
}