package cn.sqh.xierhelper.core.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.BaseFragment
import cn.sqh.xierhelper.core.ui.viewModel.CourseViewModel
import cn.sqh.xierhelper.databinding.FragmentCoursesBinding
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.LogUtils
import java.util.ArrayList

class CourseListFragment : BaseFragment() {

    val mViewModel: CourseViewModel by viewModels()

    val mDataBinding by lazy { binding as FragmentCoursesBinding }

    lateinit var viewPager: ViewPager2

    private var pageChangeCallbacks: PageChangedCallbacks? = null

    override fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        initRefreshView()
        initViewPager()
    }

    //初始化ViewPager
    private fun initViewPager() {
        viewPager = mDataBinding.viewPager
        viewPager.offscreenPageLimit = 1
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pageChangeCallbacks?.onPageChanged(position)
            }
        })
        refreshCourseTables()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pageChangeCallbacks = context as PageChangedCallbacks
    }

    override fun onDetach() {
        super.onDetach()
        pageChangeCallbacks = null
    }

    //初始化下拉刷新视图
    private fun initRefreshView() {
        mDataBinding.swipeRefreshLayout?.apply {
            setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light
            )
            LogUtils.d(mViewModel.getCurrentTerm())
            mViewModel.getCoursesByTerm()
            setOnRefreshListener {
                mViewModel.courseLiveData.observe(this@CourseListFragment) { result ->
                    LogUtils.d("进入观察了")
                    val courseInfo = result.getOrNull()
                    if (courseInfo != null) {
                        LogUtils.d("返回的课程是$courseInfo")
                    }
                    refreshCourseTables()
                    this.isRefreshing = false
                }
            }
        }
    }

    private fun refreshCourseTables() {
        mDataBinding.viewPager.adapter =
            object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int {
                    return 22
                }

                override fun createFragment(position: Int): Fragment {
                    return CourseFragment.create(position)
                }
            }
    }

    override fun getDataBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding? {
        return FragmentCoursesBinding.inflate(inflater, container, false)
    }

    interface PageChangedCallbacks {
        fun onPageChanged(pageIndex: Int)
    }

    companion object {
        fun newInstance() = CourseListFragment()
    }
}