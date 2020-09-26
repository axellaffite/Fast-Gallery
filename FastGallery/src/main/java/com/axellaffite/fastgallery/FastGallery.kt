package com.axellaffite.fastgallery

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.axellaffite.fastgallery.slider_animations.SlideAnimation
import kotlinx.android.synthetic.main.fragment_image_displayer.*

@Suppress("unused")
class FastGallery<Image> : DialogFragment() {

    /**
     * You must use this class to build FastGallery dialog.
     *
     * /!\ REQUIRED /!\
     * YOU MUST PROVIDE A CONVERTER WITH THE FUNCTION
     * `withConverter()` TO CONVERT YOUR DATA INTO VALID URIs !
     *
     * @param Image The type you will use to store images.
     */
    class Builder<Image> {

        private val fragment = FastGallery<Image>()

        /**
         * Images you want to display.
         * It can be any type as you must provide
         * a converter for these data.
         *
         * @param images The images you want to display
         */
        fun withImages(images: List<Image>) = this.apply {
            fragment.config.dataSet = images
        }

        /**
         * /!\ Required /!\
         * This converter converts your images into Uri.
         * You can see an example on the example code in the MainActivity.
         *
         * @param converter
         */
        fun withConverter(converter: (Image, ImageLoader<Image>) -> Unit) = this.apply {
            fragment.config.converter = converter
        }

        /**
         * This specify an animation for the gallery.
         * Some are available in the SlideAnimations class.
         *
         * @param animator
         */
        fun withSlideAnimation(animator: SlideAnimation) = this.apply {
            fragment.config.slideAnimation = animator
        }

        /**
         * Add a listener to the image loading operation.
         * With this, you can provide callbacks to display loading
         * icon while the image in downloading.
         *
         * @param listener
         */
        fun withOnImageLoadListener(listener: OnImageLoadListener<Image>) = this.apply {
            fragment.config.loadListener = listener
        }

        /**
         * The first image you want to display.
         *
         * @param position
         */
        fun withInitialPosition(position: Int) = this.apply {
            fragment.config.initialPosition = position
        }

        /**
         * This layout will be displayed over the
         * gallery.
         *
         * @param view
         */
        fun withOverlay(view: View) = this.apply {
            fragment.config.overlayLayout = view
        }

        /**
         * The offscreen loading limit
         *
         * @param limit
         */
        fun withOffscreenLimit(limit: Int) = this.apply {
            fragment.config.offscreenLimit = limit
        }

        /**
         * Set the background resource of the gallery.
         *
         * @param resource
         */
        fun withBackgroundResource(resource: Int) = this.apply {
            fragment.config.backgroundResource = resource
        }

        /**
         * This callback will be called when the dialog
         * is dismissed.
         *
         * @param listener
         */
        fun withOnDismissListener(listener: () -> Unit) = this.apply {
            fragment.config.onDismissListener = listener
        }

        /**
         * Check the dialog validity and returns it.
         *
         */
        fun build() = validate(fragment)


        private fun validate(fragment: FastGallery<Image>) = fragment.apply {
            if (config.isNotValid()) {
                throw InvalidImageDisplayerConfigurationException("You must provide a converter with the function `withConverter()` !")
            }
        }

    }

    class InvalidImageDisplayerConfigurationException(reason: String): IllegalStateException(reason)

    class Configuration<Image> {
        var dataSet : List<Image> = listOf()
        var converter : ((Image, ImageLoader<Image>) -> Unit)? = null
        var overlayLayout : View? = null
        var initialPosition = 0
        var onDismissListener : (() -> Unit)? = null
        var loadListener : OnImageLoadListener<Image>? = null
        var slideAnimation: ViewPager2.PageTransformer? = null
        var offscreenLimit = 1
        var backgroundResource = android.R.color.background_dark

        fun isNotValid() = !isValid()

        fun isValid() =
            converter != null
    }

    private var config = Configuration<Image>()
    private val viewModel: FastGalleryViewModel by viewModels()

    override fun onDestroy() {
        ImageLoader.cleanCache()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            setStyle(STYLE_NO_FRAME, it.packageManager.getActivityInfo(it.componentName, 0).themeResource)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_image_displayer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (config.isValid()) {
            viewModel.configuration = config
        } else {
            if (viewModel.configuration == null) {
                throw InvalidImageDisplayerConfigurationException("Please construct the ImageFragment using the FastGallery.Builder class.")
            }

            @Suppress("UNCHECKED_CAST")
            config = viewModel.configuration as Configuration<Image>
        }

        config.run {
            pager.apply {
                adapter = ImageViewPager(dataSet, converter!!).apply {
                    onImageLoadListener = config.loadListener
                }

                setCurrentItem(initialPosition, false)
                setPageTransformer(config.slideAnimation)
                setBackgroundResource(config.backgroundResource)
            }

            overlayLayout?.let {
                overlay.removeAllViews()
                overlay.addView(it)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        config.onDismissListener?.invoke()
        super.onDismiss(dialog)
    }

    fun getViewPager() = pager ?: null

}