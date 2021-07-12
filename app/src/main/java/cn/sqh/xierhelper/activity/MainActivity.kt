package cn.sqh.xierhelper.activity

//import androidx.activity.viewModels
//import kotlinx.android.synthetic.main.activity_main_test.*
//import kotlinx.android.synthetic.main.activity_main.*

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.ToolbarActivity
import cn.sqh.xierhelper.core.ui.fragment.CourseFragment
import cn.sqh.xierhelper.core.ui.viewModel.CourseViewModel
import cn.sqh.xierhelper.databinding.ActivityMainBinding
import cn.sqh.xierhelper.logic.model.StuCourseInfo
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.*


private const val TAG = "MainActivity"

class MainActivity : ToolbarActivity(), NavigationView.OnNavigationItemSelectedListener {

    override val mViewModel: CourseViewModel by viewModels()

    override val mDataBinding: ActivityMainBinding by lazy { dataBind() }

    lateinit var drawerToggle: ActionBarDrawerToggle

    lateinit var viewPager: ViewPager2

    override fun initView() {
        setSupportActionBar(mToolbar)//必须先写
        mDataBinding.viewModel = mViewModel
        mDataBinding.bottomNavigationView.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )
        mDataBinding.bottomNavigationView.selectedItemId =
            R.id.bottom_navigation_item_course//设置默认选择页为课程页
        // TODO: 2021/5/25 这里之后应该是从api校历中获取的周数然后给下面的getItemCount.size
        initWeekSelector()
        initLeftDrawer()
        initRefreshView()
        initViewPager()
    }

    //初始化ViewPager
    private fun initViewPager() {
        viewPager = mDataBinding.viewPager
        viewPager.offscreenPageLimit = 1
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mDataBinding.include.weekSelector.text = "第${position + 1}周"
            }
        })
        refreshCourseTables()
    }

    var pvOptions: OptionsPickerView<String?>? = null

    //初始化周数选择器
    private fun initWeekSelector() {
        val weekSelector = findViewById<TextView>(R.id.week_selector)
        weekSelector.setOnClickListener {
            pvOptions?.show()
        }
        val optionsItems = ArrayList<String>()
        for (weekIndex in 1..21) {
            optionsItems.add("第${weekIndex}周")
        }
        pvOptions = OptionsPickerBuilder(this, object : OnOptionsSelectListener {
            override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
//                mDataBinding.viewPager.setCurrentItem(options1, smoothScroll)
                mDataBinding.viewPager.setCurrentItem(options1)
            }
        })
            .setLayoutRes(R.layout.layout_bottom_week_selector, object : CustomListener {
                override fun customLayout(v: View?) {
                    v?.let {
                        val tvSubmit = v.findViewById<TextView>(R.id.tv_finish)
                        val ivCancel = v.findViewById<ImageView>(R.id.iv_cancel)
                        tvSubmit?.setOnClickListener {
                            pvOptions?.returnData()
                            pvOptions?.dismiss()
                        }
                        ivCancel.setOnClickListener {
                            pvOptions?.dismiss()
                        }
                    }
                }
            })
            .setTitleText("周数选择")
            .setContentTextSize(20)//设置滚轮文字大小
            .setDividerColor(Color.LTGRAY)//设置分割线的颜色
            .isRestoreItem(false)//切换时是否还原，设置默认选中第一项。
            .build<String>()
        pvOptions?.setPicker(optionsItems as List<String?>?)
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
                mViewModel.courseLiveData.observe(this@MainActivity) { result ->
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

    //初始化左抽屉
    private fun initLeftDrawer() {
        val drawerLayout = mDataBinding.drawerLayout
        //关联mToolbar
        drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.open, R.string.close)
        drawerToggle.syncState()//初始化
        drawerLayout?.let {
            drawerLayout.addDrawerListener(drawerToggle)//给布局增添抽屉式菜单按钮监听器

            //debug监听
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    Log.d(TAG, "滑动中")
                }

                override fun onDrawerOpened(drawerView: View) {
                    //滑动菜单隐藏后，同时隐藏输入法
                    val manager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(
                        drawerView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                    Log.d(TAG, "打开")
                }

                override fun onDrawerClosed(drawerView: View) {
                    val navigationView = mDataBinding.navigationView
                    if (drawerView != navigationView) {
//                        drawerToggle.isDrawerIndicatorEnabled = true
                        drawerLayout.setDrawerLockMode(
                            DrawerLayout.LOCK_MODE_UNLOCKED,
                            navigationView!!
                        )
                    }
                    Log.d(TAG, "关闭")
                }

                override fun onDrawerStateChanged(newState: Int) {
                    Log.d(TAG, "状态改变")
                    Log.d(TAG, "新状态是$newState")
                    if (newState != DrawerLayout.STATE_IDLE) {
                        drawerToggle.isDrawerIndicatorEnabled = true
                    }
                }

            })

            val navigationView = mDataBinding.navigationView
            navigationView?.let {
                //内容点击
                it.setNavigationItemSelectedListener(this)
                updateContainView(it)
            }
        }

    }

    //刷新左抽屉视图
    private fun updateContainView(navigationView: NavigationView) {
        val headerView = navigationView.getHeaderView(0)
        val tv_user_leftdrawer: TextView = headerView.findViewById(R.id.drawer_left_username)
        val tv_user_desc_leftdrawer: TextView = headerView.findViewById(R.id.drawer_left_user_desc)
        /*ActivityController.loginUser?.let {
            var currentContain = it.currentContain
            var currentString: String = ""
            if (currentContain / 1024 / 1024 >= 1) {
                currentContain = currentContain / 1024 / 1024
                currentString = "${currentContain}MB"
            } else {
                currentContain = currentContain / 1024
                currentString = "${currentContain}KB"
            }
            tv_user_leftdrawer.text = it.username.toString()
            tv_user_desc_leftdrawer.text =
                "${currentString}/${it.container / 1024 / 1024}MB"
        }*/
    }

    //底栏内容点击事件
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_navigation_item_course -> {
                    // TODO: 2021/5/1 显示课程表fragment
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_item_tools -> {
                    // TODO: 2021/5/1 显示工具箱fragment
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    //左抽屉内容点击事件
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val title = item.title as String
        Toast.makeText(this, "点击了$title", Toast.LENGTH_SHORT).show()
        when (item.itemId) {
            R.id.nav_exitLogin -> {
                Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show()
                val exitIntent = Intent(this, LoginActivity::class.java)
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                exitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mViewModel.clearLoginInfo()
                startActivity(exitIntent)
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //菜单栏点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val drawerLayout = mDataBinding.drawerLayout
                if (drawerLayout?.isDrawerOpen(GravityCompat.END) == true) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    drawerLayout?.openDrawer(GravityCompat.END)
                    drawerLayout?.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                        GravityCompat.START
                    )
                    drawerToggle.isDrawerIndicatorEnabled = false
                    drawerToggle.setToolbarNavigationClickListener {
                        drawerLayout?.closeDrawer(GravityCompat.END)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setToolbar() {
        mToolbar.setTitle(getString(R.string.login_title))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    companion object {
        fun start(context: Context, course: StuCourseInfo) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }


}
