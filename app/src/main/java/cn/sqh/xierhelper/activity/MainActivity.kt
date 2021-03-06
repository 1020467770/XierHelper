package cn.sqh.xierhelper.activity

//import androidx.activity.viewModels
//import kotlinx.android.synthetic.main.activity_main_test.*
//import kotlinx.android.synthetic.main.activity_main.*

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.ToolbarActivity
import cn.sqh.xierhelper.core.ui.fragment.CourseListFragment
import cn.sqh.xierhelper.core.ui.fragment.ToolsFragment
import cn.sqh.xierhelper.core.ui.viewModel.CourseViewModel
import cn.sqh.xierhelper.databinding.ActivityMainBinding
import cn.sqh.xierhelper.logic.model.Setting
import cn.sqh.xierhelper.logic.model.StuCourseInfo
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.*


private const val TAG = "MainActivity"

class MainActivity : ToolbarActivity(), NavigationView.OnNavigationItemSelectedListener,
    CourseListFragment.PageChangedCallbacks, CompoundButton.OnCheckedChangeListener {

    override val mViewModel: CourseViewModel by viewModels()

    override val mDataBinding: ActivityMainBinding by lazy { dataBind() }

    lateinit var drawerToggle: ActionBarDrawerToggle

    var weekSelector: TextView? = null

    var pvOptions: OptionsPickerView<String?>? = null

    private val changeSmoothSwitch by lazy { mDataBinding.rightDrawer!!.isChangeSmoothSwitch }

    private val changeScaleSwitch by lazy { mDataBinding.rightDrawer!!.isChangeScaleSwitch }

    private val settings by lazy { mViewModel.getSettings() }

    override fun initView() {
        setSupportActionBar(mToolbar)//????????????
        mDataBinding.viewModel = mViewModel
        mDataBinding.bottomNavigationView.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )
        mDataBinding.bottomNavigationView.selectedItemId =
            R.id.bottom_navigation_item_course//?????????????????????????????????
        // TODO: 2021/5/25 ????????????????????????api??????????????????????????????????????????getItemCount.size
        initLeftDrawer()
        initRightDrawer()
        initWeekSelector()
        /*initRefreshView()
        initViewPager()*/
    }

    private fun initRightDrawer() {
        changeSmoothSwitch.setOnCheckedChangeListener(this)
        changeScaleSwitch.setOnCheckedChangeListener(this)
    }

    //??????????????????
    private fun initLeftDrawer() {
        val drawerLayout = mDataBinding.drawerLayout
        //??????mToolbar
        drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.open, R.string.close)
        drawerToggle.syncState()//?????????
        drawerLayout?.let {
            drawerLayout.addDrawerListener(drawerToggle)//?????????????????????????????????????????????

            //debug??????
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    Log.d(TAG, "?????????")
                }

                override fun onDrawerOpened(drawerView: View) {
                    //?????????????????????????????????????????????
                    val manager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(
                        drawerView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                    Log.d(TAG, "??????")
                }

                override fun onDrawerClosed(drawerView: View) {
                    val leftDrawer = mDataBinding.navigationView
                    //?????????drawer????????????????????????????????????????????????????????????==????????????????????????!=?????????
                    if (drawerView != leftDrawer) {
//                        drawerToggle.isDrawerIndicatorEnabled = true
                        drawerLayout.setDrawerLockMode(
                            DrawerLayout.LOCK_MODE_UNLOCKED,
                            leftDrawer!!
                        )
                        saveSetting()
                    }
                    Log.d(TAG, "??????")
                }

                override fun onDrawerStateChanged(newState: Int) {
                    Log.d(TAG, "????????????")
                    Log.d(TAG, "????????????$newState")
                    if (newState != DrawerLayout.STATE_IDLE) {
                        drawerToggle.isDrawerIndicatorEnabled = true
                    }
                }

            })

            val navigationView = mDataBinding.navigationView
            navigationView?.let {
                //????????????
                it.setNavigationItemSelectedListener(this)
                updateContainView(it)
            }
        }

    }

    //?????????????????????
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

    //???????????????????????????
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val title = item.title as String
        Toast.makeText(this, "?????????$title", Toast.LENGTH_SHORT).show()
        when (item.itemId) {
            R.id.nav_exitLogin -> {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show()
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

    //?????????????????????
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
                    updateRightDrawerUI()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSetting() {
        settings.datas[Setting.SETTING_TYPE_IS_CHANGE_PAGE_SMOOTH] = changeSmoothSwitch.isChecked
        settings.datas[Setting.SETTING_TYPE_IS_CHANGE_PAGE_SCALE] = changeScaleSwitch.isChecked
        mViewModel.saveSettings(settings)
    }

    private fun updateRightDrawerUI() {
        val isChangeSmooth = settings.datas[Setting.SETTING_TYPE_IS_CHANGE_PAGE_SMOOTH]
        val isChangeScale = settings.datas[Setting.SETTING_TYPE_IS_CHANGE_PAGE_SCALE]
        if (isChangeSmooth is Boolean) {
            changeSmoothSwitch.isChecked = isChangeSmooth as Boolean
        }
        if (isChangeScale is Boolean) {
            changeScaleSwitch.isChecked = isChangeScale as Boolean
        }
    }

    //????????????????????????
    private fun initWeekSelector() {
        weekSelector = findViewById<TextView>(R.id.week_selector)
        weekSelector?.setOnClickListener {
            pvOptions?.show()
        }
        val optionsItems = ArrayList<String>()
        for (weekIndex in 1..21) {
            optionsItems.add("???${weekIndex}???")
        }
        pvOptions = OptionsPickerBuilder(this, object : OnOptionsSelectListener {
            override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
//                mDataBinding.viewPager.setCurrentItem(options1, smoothScroll)
                supportFragmentManager.findFragmentById(R.id.fragment_container)?.view?.findViewById<ViewPager2>(
                    R.id.view_pager
                )?.setCurrentItem(
                    options1,
                    settings.datas[Setting.SETTING_TYPE_IS_CHANGE_PAGE_SMOOTH] as Boolean
                )
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
            .setTitleText("????????????")
            .setContentTextSize(20)//????????????????????????
            .setDividerColor(Color.LTGRAY)//????????????????????????
            .isRestoreItem(false)//??????????????????????????????????????????????????????
            .build<String>()
        pvOptions?.setPicker(optionsItems as List<String?>?)
    }

    //????????????????????????
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment == null)
                false
            when (item.itemId) {
                R.id.bottom_navigation_item_course -> {
                    // TODO: 2021/5/1 ???????????????fragment
                    val fragment = CourseListFragment.newInstance()
                    weekSelector?.isVisible = true
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_item_tools -> {
                    // TODO: 2021/5/1 ???????????????fragment
                    val fragment = ToolsFragment.newInstance()
                    weekSelector?.isVisible = false
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun setToolbar() {
        mToolbar.setTitle(getString(R.string.app_name))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    companion object {
        fun start(context: Context, course: StuCourseInfo) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onPageChanged(pageIndex: Int) {
        mDataBinding.header.weekSelector.text = "???${pageIndex + 1}???"
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        buttonView?.let { switch ->
            when (switch.id) {
                R.id.isChangeSmoothSwitch -> {
//                    ToastUtils.showShort("?????????????????????")
                }
                R.id.isChangeScaleSwitch -> {
//                    ToastUtils.showShort("?????????????????????")
                }
            }
        }
    }
}
