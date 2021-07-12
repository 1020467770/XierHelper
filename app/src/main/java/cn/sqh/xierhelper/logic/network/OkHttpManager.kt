package cn.sqh.xierhelper.logic.network

import cn.sqh.xierhelper.XierHelperApplication
import cn.sqh.xierhelper.logic.network.cookie.MyPersistentCookieJar
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object OkHttpManager {

    val okHttpClient = genericClient()

    private fun genericClient(): OkHttpClient {

        val cookieJar: ClearableCookieJar =
            MyPersistentCookieJar(
                SetCookieCache(),
                SharedPrefsCookiePersistor(XierHelperApplication.context)
            )

        val httpClient = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
//                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")

                .addHeader("Accept-Encoding", "gzip, deflate")

                .addHeader("Connection", "keep-alive")

                .addHeader("Accept", "*/*")

                .build()
//            LogUtils.d(request.headers())
//            LogUtils.d(cookieJar.loadForRequest(request.url()))

            cookieJar.saveFromResponse(request.url(), cookieJar.loadForRequest(request.url()))

//            LogUtils.d(request.url())
            val response = chain.proceed(request)
//            LogUtils.d(response.headers())
            response
        })
            .cookieJar(cookieJar)//使用cookieJar设置Cookie持久化
            .build()//buildOkHttp
//        cookieJar.clear()
        return httpClient

    }
}