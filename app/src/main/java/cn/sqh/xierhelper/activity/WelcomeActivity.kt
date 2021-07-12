package cn.sqh.xierhelper.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.ui.adapter.SimpleAdapter
import cn.sqh.xierhelper.core.ui.transform.DepthPageTransformer
import cn.sqh.xierhelper.core.ui.viewModel.CourseViewModel
import cn.sqh.xierhelper.logic.model.CourseTableItem
import cn.sqh.xierhelper.logic.model.CustomBean
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.utils.BannerUtils
import com.zhpan.indicator.enums.IndicatorSlideMode
import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var mViewPager: BannerViewPager<CustomBean>

    private val des = arrayOf("啊\n真好看", "呀\n针不戳", "哇\n真厉害")

    val viewModel: CourseViewModel by viewModels()

    var isDaoFinished: Boolean = false

    var courseList: List<CourseTableItem>? = null

    private var mDrawableList: MutableList<Int> = ArrayList()

    private val data: List<CustomBean>
        get() {
            val list = ArrayList<CustomBean>()
            for (i in mDrawableList.indices) {
                val customBean = CustomBean()
                customBean.imageRes = mDrawableList[i]
                customBean.imageDescription = des[i]
                list.add(customBean)
            }
            return list
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        initData()
        viewModel.courseListLiveDateFromLocal.observe(this) { courses ->
            if (courses.size > 0 && courses != null) {
                courses.let {
//                LogUtils.d("从数据库中获取的courses=" + courses + "size=${courses.size}")
                    viewModel.setStuCourseInfo(courses)
                    courseList = courses
                }
            }
            isDaoFinished = true
        }
        setupViewPager()
        updateUI(0)

    }

    private fun initData() {
        for (i in 0..2) {
            val drawable = resources.getIdentifier("guide$i", "drawable", packageName)
            mDrawableList.add(drawable)
        }
    }

    private fun setupViewPager() {
        mViewPager = findViewById(R.id.viewpager)
        mViewPager.apply {
            setCanLoop(false)
            setPageTransformer(DepthPageTransformer())
            setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_100).toInt())
            setIndicatorSliderGap(resources.getDimension(R.dimen.dp_10).toInt())
            setIndicatorSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorSliderRadius(
                resources.getDimension(R.dimen.dp_3).toInt(),
                resources.getDimension(R.dimen.dp_4_5).toInt()
            )
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    BannerUtils.log("position:$position")
                    updateUI(position)
                }
            })
            adapter = SimpleAdapter()
            setIndicatorSliderColor(
                ContextCompat.getColor(this@WelcomeActivity, R.color.white),
                ContextCompat.getColor(this@WelcomeActivity, R.color.white_alpha_75)
            )
        }.create(data)
    }

    fun onClick(view: View) {
        if (!isDaoFinished) {
            return
        }
        if (courseList.isNullOrEmpty()) {
            LoginActivity.start(this)
        } else {
            viewModel.refreshUserInfoIntoMemory()
            MainActivity.start(this, viewModel.getStuCourseInfo())
        }
        finish()
    }

    private fun updateUI(position: Int) {
        tv_describe?.text = des[position]
        val translationAnim = ObjectAnimator.ofFloat(tv_describe, "translationX", -120f, 0f)
        translationAnim.apply {
            duration = ANIMATION_DURATION.toLong()
            interpolator = DecelerateInterpolator()
        }
        val alphaAnimator = ObjectAnimator.ofFloat(tv_describe, "alpha", 0f, 1f)
        alphaAnimator.apply {
            duration = ANIMATION_DURATION.toLong()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnim, alphaAnimator)
        animatorSet.start()

        if (position == mViewPager.data.size - 1 && btn_start?.visibility == View.GONE) {
            btn_start?.visibility = View.VISIBLE
            ObjectAnimator
                .ofFloat(btn_start, "alpha", 0f, 1f)
                .setDuration(ANIMATION_DURATION.toLong()).start()
        } else {
            btn_start?.visibility = View.GONE
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 1300
    }
}