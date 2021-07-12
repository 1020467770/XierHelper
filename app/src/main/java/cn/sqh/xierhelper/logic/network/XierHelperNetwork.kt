package cn.sqh.xierhelper.logic.network

import android.util.Log
import cn.sqh.xierhelper.logic.network.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object XierHelperNetwork {

    private val mUserService = ServiceCreator.create<UserService>()

    suspend fun loginOnJiaoWuchu(muser: String, passwd: String, verifyCode: String) =
        mUserService.loginOnJiaoWuchu(muser, passwd, verifyCode).await()

    suspend fun ssoLoginStepOne(redirectUrl: String) =
        mUserService.ssologinStepOne(redirectUrl).await()

    suspend fun ssoLoginStepTwo(token: String) = mUserService.ssologinStepTwo(token).await()
    suspend fun loginCheck_xs(url: String) = mUserService.loginCheck_xs(url).await()
    suspend fun getCourseById(id: String) = mUserService.getCourseById(id).await()
    suspend fun getXiaoli() = mUserService.getXiaoli().await()

    private const val TAG = "XierHelperNetwork"

    private suspend fun <T> Call<T>.await(): Response<T> {
        return suspendCoroutine { continuation -> //suspendCoroutine会把当前协程立即挂起，而lambda表达式的代码则会在普通线程中执行
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    Log.d(TAG, "onSuccessResponse: ${response}")
                    if (body != null) continuation.resume(response)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
//                    t.printStackTrace()
                    Log.d(TAG, "onFailedResponse: ${t.message}")
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}