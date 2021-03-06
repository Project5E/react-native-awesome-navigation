package com.project5e.react.navigation.utils

import android.content.Context
import android.graphics.Bitmap
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.views.imagehelper.ImageSource
import com.project5e.react.navigation.data.react.ImageResolvedAssetSource

fun getImageSource(context: Context, source: ImageResolvedAssetSource?): ImageSource {
    val uri = source?.uri
    val width = source?.width ?: 0.0
    val height = source?.height ?: 0.0
    return ImageSource(context, uri, width.toDouble(), height.toDouble())
}

fun ImageSource.load(
    context: Context,
    onSucceed: ((bitmap: Bitmap?) -> Unit)? = null,
    onFailure: ((dataSource: DataSource<CloseableReference<CloseableImage>>?) -> Unit)? = null,
) {
    val request = ImageRequestBuilder.newBuilderWithSource(uri).build()
    Fresco.getImagePipeline().fetchDecodedImage(request, context).subscribe(object : BaseBitmapDataSubscriber() {
        override fun onNewResultImpl(bitmap: Bitmap?) {
            UiThreadUtil.runOnUiThread {
                onSucceed?.invoke(bitmap)
            }
        }

        override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
            UiThreadUtil.runOnUiThread {
                onFailure?.invoke((dataSource))
            }
        }

    }, CallerThreadExecutor.getInstance())
}
