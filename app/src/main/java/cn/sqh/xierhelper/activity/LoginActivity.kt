package cn.sqh.xierhelper.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.ToolbarActivity
import cn.sqh.xierhelper.core.ui.viewModel.CourseViewModel
import cn.sqh.xierhelper.core.ui.viewModel.LoginUserViewModel
import cn.sqh.xierhelper.dao.Repository
import cn.sqh.xierhelper.databinding.ActivityLoginBinding
import cn.sqh.xierhelper.logic.model.StuCourseInfo
import cn.sqh.xierhelper.logic.network.module.GlideApp
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy

//验证码url
private const val url = "https://jwcjwxt2.fzu.edu.cn:82/plus/verifycode.asp"

class LoginActivity : ToolbarActivity() {

    override val mViewModel: LoginUserViewModel by viewModels()

    val courseViewModel: CourseViewModel by viewModels()

    override val mDataBinding: ActivityLoginBinding by lazy { dataBind() }

    override fun initView() {
        mDataBinding.viewModel = mViewModel
        refreshVerifyImg()
        mViewModel.loginUserLiveData.observe(this, Observer { result ->
            val user = result.getOrNull()
            if (user != null) {
                courseViewModel.getCoursesByTerm("202001")
                LogUtils.d("返回的user是$user")
            }
        })
        courseViewModel.courseLiveData.observe(this, Observer { result ->
            LogUtils.d("进入观察了")
            val courseInfo = result.getOrNull()
            LogUtils.d("courseInfo=$courseInfo")
            if (courseInfo != null) {
                LogUtils.d("返回的课程是$courseInfo")
                MainActivity.start(this, courseInfo)
                courseViewModel.refreshAllCourses(courseInfo.courses)
                finish()
            }
        })
    }

    override fun setToolbar() {
        mToolbar.setTitle(getString(R.string.login_title))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    //刷新验证码图片
    private fun refreshVerifyImg() {
        //用HTTPGlideModule注册的GlideApp可以自定义OkHttp规则，如存取Cookie
        GlideApp.with(applicationContext)
            .load(url)
            .skipMemoryCache(true)   //验证码不缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(mDataBinding.verifyImg)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

}