package cn.sqh.xierhelper.logic.network

import android.util.Log
import cn.sqh.xierhelper.XierHelperApplication
import com.blankj.utilcode.util.LogUtils
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceCreator {

    private const val BASE_URL = "https://jwcjwxt2.fzu.edu.cn:81/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpManager.okHttpClient)
        .build()

    fun <T> tempRequestCreate(url: String, serviceClass: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(OkHttpManager.okHttpClient)
            .build()
        return retrofit.create(serviceClass)
    }

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

    inline fun <reified T> tempRequestCreate(url: String): T = tempRequestCreate(url, T::class.java)

}