package cn.sqh.xierhelper.logic.network.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @FormUrlEncoded
    @Headers("Host:jwcjwxt2.fzu.edu.cn:82", "Referer:https://jwch.fzu.edu.cn/")
    @POST("https://jwcjwxt2.fzu.edu.cn:82/logincheck.asp")
    fun loginOnJiaoWuchu(
        @Field("muser") muser: String,
        @Field("passwd") passwd: String,
        @Field("Verifycode") verifyCode: String
    ): Call<ResponseBody>

    @GET("")
    @Headers("Host:jwcjwxt2.fzu.edu.cn")
    fun ssologinStepOne(@Url url: String): Call<ResponseBody>

    @FormUrlEncoded
    @Headers("X-Requested-With:XMLHttpRequest", "Host:jwcjwxt2.fzu.edu.cn")
    @POST("https://jwcjwxt2.fzu.edu.cn/Sfrz/SSOLogin")
    fun ssologinStepTwo(
        @Field("token") token: String
    ): Call<ResponseBody>

    @GET("")
    @Headers(
        "authority:jwcjwxt2.fzu.edu.cn:81",
        "Host:jwcjwxt2.fzu.edu.cn:81",
        "sec-fetch-site:same-site",
        "sec-fetch-mode:navigate",
        "sec-fetch-dest:document",
        "sec-ch-ua:\"Chromium\";v=\"88\", \"Google Chrome\";v=\"88\", \";Not A Brand\";v=\"99\"",
        "sec-ch-ua-mobile:?0",
        "upgrade-insecure-requests:1",
        "referer:https://jwcjwxt2.fzu.edu.cn/",
        "origin:https://jwch.fzu.edu.cn"

    )
    fun loginCheck_xs(@Url url: String): Call<ResponseBody>

    @GET("/student/xkjg/wdkb/kb_xs.aspx")
    fun getCourseById(@Query("id") id: String): Call<ResponseBody>

    @GET("")
    fun getXiaoli(@Url url:String = "https://jwcjwxt1.fzu.edu.cn/xl.asp"): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/student/xkjg/wdkb/kb_xs.aspx")
    fun getCourseByOtherTerm(
        @Field("ctl00\$ContentPlaceHolder1\$DDL_xnxq") term: String,
        @Field("__VIEWSTATE") token1: String,
        @Field("__EVENTVALIDATION") token2: String
    ): Call<ResponseBody>
}