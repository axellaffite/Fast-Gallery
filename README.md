# Fast Gallery  
Based on well known libraries such as [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view) and [Picasso](https://github.com/square/picasso), this library allows you to display images at their full resolutions.  

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
You can use it with every types you want. You just have to provide a converter which converts your types into Uri(s).  

Here is an example using a String List :

```kotlin
// A list of links
val images = listOf (
    "https://images.unsplash.com/photo-1600656862818-db82cdb7d409?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
    "https://images.unsplash.com/photo-1600758946933-422348d8b5d4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1920&q=80",
    "https://images.unsplash.com/photo-1427847907429-d1ba99bf013d?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1920&fit=max&ixid=eyJhcHBfaWQiOjF9"
)


FastGallery.Builder<String>()
    .withImages(images) // The dataset
    .withInitialPosition(0) // The first image to display
    .withSlideAnimation(SlideAnimations.depthPageAnimation())
    .withBackgroundResource(R.color.colorGalleryBackground)
    .withOffscreenLimit(2)
    // This part is required. It allows the library
    // to convert your data into URI(s).
    .withConverter { 
        Uri.parse(it)
    }
    .build()
    // As the library use a FragmentDialog, you must 
    // provide a FragmentManager and a tag to show it.
    .show(supportFragmentManager, "theTagYouWant") 
```

And here is the result :  
<img src="https://github.com/axellaffite/Fast-Gallery/blob/master/previews/FastGallery.gif?raw=true" width=500>
