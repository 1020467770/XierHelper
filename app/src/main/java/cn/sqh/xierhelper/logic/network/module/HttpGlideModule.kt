package cn.sqh.xierhelper.logic.network.module

import android.content.Context
import cn.sqh.xierhelper.logic.network.OkHttpManager
import cn.sqh.xierhelper.logic.network.ServiceCreator
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.io.InputStream

@GlideModule
class HttpGlideModule : AppGlideModule() {
    /*override fun isManifestParsingEnabled(): Boolean {
        return false
    }*/

    /*override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
        builder.setDefaultRequestOptions(requestOptions)
    }*/

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(OkHttpManager.okHttpClient)
        )
    }
}