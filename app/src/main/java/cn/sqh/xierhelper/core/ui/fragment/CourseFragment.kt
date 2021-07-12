package cn.sqh.xierhelper.core.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.BaseFragment
import cn.sqh.xierhelper.core.ui.customView.ButtonM
import cn.sqh.xierhelper.core.ui.viewModel.CourseViewModel
import cn.sqh.xierhelper.databinding.FragmentCoursePageBinding
import cn.sqh.xierhelper.logic.model.Course
import cn.sqh.xierhelper.logic.model.CourseTableItem
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import java.util.*

private const val KEY_WEEK_INDEX = "cn.sqh.xierhelper.core.ui.course.KEY_WEEK_INDEX"

private const val TAG = "CourseFragment"

class CourseFragment : BaseFragment() {

    val viewModel: CourseViewModel by viewModels()

    lateinit var tabItemHolder: TabItemViewHolder

    //从1开始的索引，保存当前课表是第几周的信息
    val currentPageWeekIndex by lazy {
        arguments?.getInt(KEY_WEEK_INDEX) ?: throw IllegalStateException("没有周数信息")
    }

    val mDataBinding by lazy { binding as FragmentCoursePageBinding }

    override fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        //测试用tv
        /*val tv = mDataBinding.tvTest
        tv.text = (0..10).random().toString()*/
        initTabView()
        initCourseTable()
    }

    //初始化课程表
    private fun initCourseTable() {
        val stuCourseInfo = viewModel.getStuCourseInfo()
        val courses = stuCourseInfo.courses
        val oneTvLeft = mDataBinding.tvLeft1
        var height = 0
        val vto = oneTvLeft.viewTreeObserver
//        LogUtils.d("currentPageWeek=$currentPageWeekIndex")
        for (course in courses) {
            if (isCourseShouldBeShow(course)) {
                val button = ButtonM(context)
                when (course.classDay) {
                    1 -> mDataBinding.mondayLayout.addView(button)
                    2 -> mDataBinding.tuesdayLayout.addView(button)
                    3 -> mDataBinding.wednesdayLayout.addView(button)
                    4 -> mDataBinding.thursdayLayout.addView(button)
                    5 -> mDataBinding.fridayLayout.addView(button)
                    6 -> mDataBinding.saturdayLayout.addView(button)
                    7 -> mDataBinding.sundayLayout.addView(button)
                }
                vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        oneTvLeft.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        height = oneTvLeft.measuredHeight
//                    LogUtils.d("这个时候$height")
                        refreshButtonView(button, height, course)
                        viewModel.saveTvMeasuredHeight(height)
                    }
                })
                if (viewModel.isSavedTvMeasuredHeight()) {
                    height = viewModel.getTvMeasuredHeight()!!
                    refreshButtonView(button, height, course)
//                LogUtils.d("repository存的$height")
                }
            }
        }
    }

    //判断课程的周数是否符合当前页以及单双周判断
    private fun isCourseShouldBeShow(
        course: CourseTableItem
    ): Boolean {
        val splitedWeekStr = course.weeks.split("-")
//        LogUtils.d("splitedWeekStr=$splitedWeekStr")
        val lowerWeek = splitedWeekStr.get(0).toIntOrNull()
        val upperWeek = splitedWeekStr.get(1).toIntOrNull()
        if (lowerWeek == null || upperWeek == null ||
            currentPageWeekIndex < lowerWeek || currentPageWeekIndex > upperWeek
        ) {
            return false
        }
        if ((course.courseType == Course.CourseType.DOUBLEWEEK && currentPageWeekIndex % 2 == 1) ||
            (course.courseType == Course.CourseType.SINGLEWEEK && currentPageWeekIndex % 2 == 0)
        ) {
            return false
        }
        return true
    }

    //刷新layout里的Button视图
    private fun refreshButtonView(
        button: ButtonM,
        height: Int,
        course: CourseTableItem
    ) {
        val layoutParams = button.layoutParams as RelativeLayout.LayoutParams
//        button.background = resources.getDrawable()
        button.setTextColori(Color.WHITE)
        button.setFillet(true)
        button.setRadius(18f)
        button.setBackColor(course.itemColor)
//        button.setBackColor(Color.parseColor("#00C2EB"))
        button.setBackColorSelected(Color.GRAY)
        button.setOnClickListener {
            ToastUtils.showShort("点击了按钮")
        }
//        context?.let { ContextCompat.getDrawable(it, R.drawable.shape_button_radius) }
//        button.setBackgroundColor(course.itemColor)
        button.setText("${course.name}\n${course.location}")
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7f); //6SP
        layoutParams.setMargins(3, (course.classOrder - 1) * height, 3, 0)
        layoutParams.height = course.classLastTime * height - 5
        button.layoutParams = layoutParams
    }

    //初始化Tab表头
    @SuppressLint("SetTextI18n")
    private fun initTabView() {
        /*val currentPageWeekIndex =
            arguments?.getInt(KEY_WEEK_INDEX) ?: throw IllegalStateException("没有周数信息")*/
        val tCalendar = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()
        tCalendar.time = viewModel.getStuCourseInfo().courseStartDate
//        LogUtils.d("date=" + Repository.currentTermCourseInfo.courseStartDate)
        tCalendar.add(Calendar.DAY_OF_WEEK_IN_MONTH, currentPageWeekIndex - 1)
        mDataBinding.monthNum.text = "${(tCalendar.get(Calendar.MONTH) + 1)}月"
        val tabLayout = mDataBinding.tabLayout
        tabLayout.isClickable = false
        for (i in 0 until 7) {
            //获取tabView
            tabLayout.addTab(tabLayout.newTab(), false)
            val tabView = tabLayout.getTabAt(i)!!
            //给tabVIew填充自定义View
            tabView.setCustomView(R.layout.tab_item)
            (tabView.customView as View).setOnClickListener {
                Toast.makeText(context, "试试", Toast.LENGTH_SHORT)
                    .apply { setGravity(Gravity.TOP, 0, 5) }.show()
//                ToastUtils.showShort("点击日期的额外功能还未完成")
            }
            //给tabView创建Holder并填充数据
            tabItemHolder = TabItemViewHolder(tabView.customView!!)
            tabItemHolder.mTabItemWeekNum.text = weekNums[i]
            tabItemHolder.mTabItemDayNum.text = tCalendar.get(Calendar.DAY_OF_MONTH).toString()
            if (currentCalendar.get(Calendar.DAY_OF_YEAR) == tCalendar.get(Calendar.DAY_OF_YEAR)) {
                tabView.select()
            }
            tCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    override fun getDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ViewDataBinding {
        return FragmentCoursePageBinding.inflate(inflater, container, false)
    }

    inner class TabItemViewHolder(tabView: View) {
        var mTabItemWeekNum: TextView =
            tabView.findViewById<View>(R.id.tab_item_week_num) as TextView
        var mTabItemDayNum: TextView =
            tabView.findViewById<View>(R.id.tab_item_day_num) as TextView
    }

    companion object {

        private val weekNums: List<String> = listOf("一", "二", "三", "四", "五", "六", "日")

        fun create(weekIndex: Int) =
            CourseFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(KEY_WEEK_INDEX, weekIndex + 1)
                }
            }
    }
}