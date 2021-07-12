package cn.sqh.xierhelper.core.ui

import android.view.View
import android.view.View.*
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cn.sqh.xierhelper.logic.network.module.GlideApp
import com.bumptech.glide.load.engine.DiskCacheStrategy


object BindingAdapters {

    @JvmStatic
    @BindingAdapter("image_from_url_nocache")
    fun setImageFromUrlWithNoCache(imageView: ImageView, url: String) {
        GlideApp.with(imageView.context)
            .load(url)
            .skipMemoryCache(true)//图片不缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)//图片不缓存
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("image_from_url")
    fun setImageFromUrl(imageView: ImageView, url: String) {
        GlideApp.with(imageView.context)
            .load(url)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("visibleOrGone")
    fun View.setVisibleOrGone(show: Boolean) {
        visibility = if (show) VISIBLE else GONE
    }

    @JvmStatic
    @BindingAdapter("visible")
    fun View.setVisible(show: Boolean) {
        visibility = if (show) VISIBLE else INVISIBLE
    }

}