# Fast Gallery  
Based on the [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view) this library allows you to display images at their full resolutions.  

It also allows caching images when they are downloaded from the Web.

## Implementation

First, add the maven repository to your root build.gradle.

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Then add the dependency to your app build.gradle :

Current version: [![](https://jitpack.io/v/axellaffite/Fast-Gallery.svg)](https://jitpack.io/#axellaffite/Fast-Gallery)

```
dependencies {
    implementation 'com.github.User:Repo:CurrentVersion'
}
```

## Usage

The library is easy to use.  
You can use it with every types you want.  

Here is an example using a String List :

```kotlin
// A list of links
private val images = listOf (
    "https://images.unsplash.com/photo-1600656862818-db82cdb7d409?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
    "https://images.unsplash.com/photo-1600758946933-422348d8b5d4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
    "https://images.unsplash.com/photo-1427847907429-d1ba99bf013d?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1920&fit=max&ixid=eyJhcHBfaWQiOjF9",
    "https://upload.wikimedia.org/wikipedia/commons/c/cc/ESC_large_ISS022_ISS022-E-11387-edit_01.JPG",
    "https://upload.wikimedia.org/wikipedia/commons/4/4e/Pleiades_large.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/3/3d/LARGE_elevation.jpg",
)


FastGallery.Builder<String>()
    .withImages(images)
    .withInitialPosition(0)
    .withSlideAnimation(SlideAnimations.depthPageAnimation())
    .withBackgroundResource(R.color.colorGalleryBackground)
    .withOffscreenLimit(1)
    .withOnImageLoadListener(object: OnImageLoadListener<String>() {
        override fun onError(e: Exception?) {
            e?.printStackTrace()
        }
    })
    .withConverter { imgSource: String, loader: ImageLoader<String> ->
        lifecycleScope.launchWhenResumed {
            loader.fromURL(imgSource, true)
        }
    }
    .build()
    .show(supportFragmentManager, "theTagYouWant")
```

And here is the result :  
<img src="https://github.com/axellaffite/Fast-Gallery/blob/master/previews/FastGallery.gif?raw=true" width=500>


### ImageLoader

This class allows you to easilly load your images from multiple sources (bitmap, url, file and Android resources).  

You should not use the bitmap one as it doesn't allows the sub-sampling optimization to be performed. If you really want to use it, just take care of resizing the images to optimize the render.  

The only function that needs to be discussed here is the `fromURL()` one.  
By default, the program will try to cache the images in the phone's memory. You can disable this option but the sub-sampling optimization cannot be done without caching the file.  
Once the Gallery is closed, the cached images are deleted.  