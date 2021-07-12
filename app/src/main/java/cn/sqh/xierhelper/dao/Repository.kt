package cn.sqh.xierhelper.dao

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import cn.sqh.xierhelper.dao.database.CourseDatabase
import cn.sqh.xierhelper.dao.sharedPreference.UserDao
import cn.sqh.xierhelper.logic.model.Course
import cn.sqh.xierhelper.logic.model.CourseTableItem
import cn.sqh.xierhelper.logic.model.StuCourseInfo
import cn.sqh.xierhelper.logic.model.User
import cn.sqh.xierhelper.logic.network.XierHelperNetwork
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Response
import java.lang.Exception
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

const val XIAOLI_VALUE_PREFIX_TERM_COUNT = 6
const val XIAOLI_VALUE_PREFIX_TERM_DATE_COUNT = 8

private const val TAG = "Repository"

object Repository {

    //课程数据库
    private val mDatabase = CourseDatabase.getDatabase()

    //课程数据库交互对象
    private val mCourseDao = mDatabase.courseDao()

    //跟数据库交互的新线程，用于insert和update
    private val mDaoExecutor = Executors.newSingleThreadExecutor()

    //教务处用的登录的Id
    private var mLoginId = ""

    //当前学期
    private var mCurrentTerm = ""

    //未格式化为项目需要的普通Course数据结构
    private var mCourseList = ArrayList<Course>()

    //当前选择的学期课程对象
    lateinit var currentTermCourseInfo: StuCourseInfo

    val admissionYear = 2019

    //存放可供选择的学期字符串
    var termList = ArrayList<String>()

    //左边侧栏每一格的高度
    var leftTvMeasuredHeight: Int? = null

    /////////////////////////////////////
    //网络请求的方法
    /**
     * 普通版本的适配协程域的登录写法
     */
    fun login(user: User, verifyCode: String) = fire_normal(Dispatchers.IO) {
        val muser = user.muser
        val passwd = user.password
        coroutineScope {
            val loginResult1 = async {
                XierHelperNetwork.loginOnJiaoWuchu(muser, passwd, verifyCode)
            }.await()
            val redirectUrl = loginResult1.raw().request().url().toString()
            LogUtils.d(redirectUrl)
            var token = ""
            var returnUrl = ""
            if (redirectUrl.contains("token=") && redirectUrl.contains("returnurl=")) {
                token = redirectUrl.substringAfter("token=").substringBefore("&")
                returnUrl = redirectUrl.substringAfter("returnurl=")
                val stringBuffer = StringBuffer(returnUrl)
                val insertString =
                    stringBuffer.insert(stringBuffer.indexOf(".aspx") + 5, "?")
                returnUrl = insertString.toString()
            } else {
                LogUtils.d("协程中途结束了")
                throw Exception("用户名或密码或验证码有误")
            }
            LogUtils.d(redirectUrl)
            LogUtils.d(token)
            LogUtils.d(returnUrl)
            val ssoLoginStepTwo = async {
                XierHelperNetwork.ssoLoginStepTwo(token)
            }.await()
            val loginCheck_xs_result = async {
                XierHelperNetwork.loginCheck_xs(returnUrl)
            }.await()
            val responseStr = loginCheck_xs_result.toString()
            if (!responseStr.contains("id=")) {
                throw Exception("SSO验证失败")
            } else {
                mLoginId =
                    responseStr.substringAfterLast("id=")
                        .substringBeforeLast("&hosturl")
            }
        }

    }

    /**
     * LiveData版本的适配协程域的登录写法
     */
    fun loginWithLiveData(user: User, verifyCode: String) = fire_liveData(Dispatchers.IO) {
        val muser = user.muser
        val passwd = user.password
        coroutineScope {
            val loginResult1 = async {
                XierHelperNetwork.loginOnJiaoWuchu(muser, passwd, verifyCode)
            }.await()
            val redirectUrl = loginResult1.raw().request().url().toString()
            LogUtils.d(redirectUrl)
            var token = ""
            var returnUrl = ""
            if (redirectUrl.contains("token=") && redirectUrl.contains("returnurl=")) {
                token = redirectUrl.substringAfter("token=").substringBefore("&")
                returnUrl = redirectUrl.substringAfter("returnurl=")
                val stringBuffer = StringBuffer(returnUrl)
                val insertString =
                    stringBuffer.insert(stringBuffer.indexOf(".aspx") + 5, "?")
                returnUrl = insertString.toString()
            } else {
                LogUtils.d("协程中途结束了")
                throw Exception("用户名或密码或验证码有误")
            }
            LogUtils.d(redirectUrl)
            LogUtils.d(token)
            LogUtils.d(returnUrl)
            val ssoLoginStepTwo = async {
                XierHelperNetwork.ssoLoginStepTwo(token)
            }.await()
            val loginCheck_xs_result = async {
                XierHelperNetwork.loginCheck_xs(returnUrl)
            }.await()
            val responseStr = loginCheck_xs_result.toString()
            if (!responseStr.contains("id=")) {
                throw Exception("SSO验证失败")
            } else {
                mLoginId =
                    responseStr.substringAfterLast("id=")
                        .substringBeforeLast("&hosturl")
            }
        }
        user.loginId = mLoginId
        saveUserToFile(user)
        Result.success(user)
    }

    fun getStuCourse(term: String) = fire_liveData(Dispatchers.IO) {
        LogUtils.d("进入了")
        coroutineScope {
            //开协程获取校历和获取课表
            val deferredCourses = async {
                XierHelperNetwork.getCourseById(mLoginId)
            }
            val deferredXiaoli = async {
                XierHelperNetwork.getXiaoli()
            }
            val courseResponse = deferredCourses.await()
            val xiaoliResponse = deferredXiaoli.await()
//            LogUtils.d(courseResponse.body()?.string())
            //开协程解析xml
            val deferredCoursesParseResult = async {
                parseCourses(courseResponse)
            }
            val deferredXiaoliParseResult = async {
                parseXiaoli(xiaoliResponse)
            }
            val tCourseList = deferredCoursesParseResult.await()
            val courseStartDate = deferredXiaoliParseResult.await()
            val formatedCourseList = async {
                val formatCourses = formatCourses(tCourseList, courseStartDate)
                addColorForCourses(formatCourses)
                formatCourses
            }.await()
            currentTermCourseInfo = StuCourseInfo(formatedCourseList, courseStartDate)
            Result.success(currentTermCourseInfo)
        }

    }

    /**
     * 给课程上色，相同名字就同一种颜色
     */
    private fun addColorForCourses(formatCourses: ArrayList<CourseTableItem>) {
        formatCourses.groupBy { course ->
            course.name
        }.values.map {
//            LogUtils.d("groupedCourses=${it}")
            val randomColor = ColorUtils.getRandomColor()
            ColorUtils.getRandomColor()
            it.forEach {
                it.itemColor = randomColor
            }
        }
    }

    fun getCourseStartDateStr(prefixStr: String) =
        termList.filter { it.startsWith(prefixStr) }.getOrNull(0)
            ?.drop(XIAOLI_VALUE_PREFIX_TERM_COUNT)?.take(XIAOLI_VALUE_PREFIX_TERM_DATE_COUNT)


    //将请求获取的课程转换成本项目方便使用的课程数据结构
    private fun formatCourses(courses: List<Course>, courseStartDate: Date) =
        ArrayList<CourseTableItem>().apply {
            courses.groupBy { course ->
                CourseTableItem(course, courseStartDate)
            }.entries.forEach {
                it.key.classLastTime = it.value.size
                it.key.classOrder = it.value.minOf { course ->
                    course.classOrder
                }
                this.add(it.key)
            }
        }

    //解析请求获取的校历Xml成可用的数据结构
    @SuppressLint("SimpleDateFormat")
    private fun parseXiaoli(response: Response<ResponseBody>): Date {
        if (!response.isSuccessful) {
            throw RuntimeException("response failed 信息：${response}")
        }
        val responseBodyHtml = response.body()?.string()
//        Log.d(TAG, "parseXiaoli: responseBodyHtml$responseBodyHtml")
        val document = Jsoup.parse(responseBodyHtml)
//        Log.d(TAG, "parseXiaoli: 解析的document如下$document")
        val terms_option = document.select("option")
//        Log.d(TAG, "parseXiaoli: 解析的option如下$terms_option")
        mCurrentTerm = terms_option.get(0).text().take(XIAOLI_VALUE_PREFIX_TERM_COUNT)
        termList = terms_option.map { it.attr("value") }.filter { value ->
            val term = value.take(6).toInt()
            val year = term / 100
            year >= admissionYear && term <= mCurrentTerm.toInt()
        } as ArrayList<String>
        val currentTermStartDateStr = termList.get(0)//2020022021030120210709
            .drop(XIAOLI_VALUE_PREFIX_TERM_COUNT)//去掉前6位的学期
            .take(XIAOLI_VALUE_PREFIX_TERM_DATE_COUNT)//取出8位作为日期
//        LogUtils.d(termList)
//        LogUtils.d(courseStartDate)
        saveCurrentTermToFile(mCurrentTerm)
        return SimpleDateFormat("yyyyMMdd").parse(currentTermStartDateStr)
    }

    //解析请求获取的课程Xml成课程表
    private fun parseCourses(response: Response<ResponseBody>): ArrayList<Course> {
        if (!response.isSuccessful) {
            throw RuntimeException("response failed 信息：${response}")
        }
        val responseBodyHtml = response.body()?.string()
//        Log.d(TAG, "parseXiaoli: 解析的responseBodyHtml如下$responseBodyHtml")
        val document = Jsoup.parse(responseBodyHtml)
//        Log.d(TAG, "parseXiaoli: 解析的document如下$document")
        val tableList = document.select("table").get(3).select("tr")
//        Log.d(TAG, "parseXiaoli: 解析的tableList如下$tableList")
//        LogUtils.d("解析的tableList如下${tableList}")
        val tCourseList = ArrayList<Course>(50)
        for (i in tableList.indices) {
            if (i >= 1 && i <= 11) {//选出第1到11行
                val row = tableList.get(i)
                val tds = row.select("td[align='center']")
//                LogUtils.d("解析的tds如下${tds}")
                val tCourse = Course()//Course实例在这里创建list会覆盖
//                var tClassOrder = ""
//                var tClassTime = ""
                for (i in 0..7) {
                    //新创建的Course实例必须放在这个for循环里
//                    val tCourse = Course()
                    val html = tds.get(i).html()
//                    Log.d(TAG, "parseXiaoli: 解析的html如下$html")
                    val splitedHtml = html.split("<br>")
//                    Log.d(TAG, "parseXiaoli: 解析的splitedHtml如下$splitedHtml")
                    if (i == 0) {
                        //xml解析的第一行为课程次序和上课时间
                        tCourse.classOrder = splitedHtml.get(0).toInt()
                        tCourse.classTime = splitedHtml.get(1)
                    }
                    tCourse.classDay = i
                    for (i in splitedHtml.indices) {
                        val splitItem = splitedHtml.get(i)
                        when (i % 5) {
                            0 -> {//设置课程名
                                val formatName =
                                    splitItem.substringAfter(">")
                                        .substringBeforeLast("<")
//                                    Log.d(TAG, "parseCourses: formatName=${formatName}")
                                tCourse.name = formatName
                            }
                            1 -> {
                                // TODO: 2021/4/26 NULL
                            }
                            2 -> {//设置单双周和教学地点
                                if (splitItem.contains("[单]")) {
                                    tCourse.courseType =
                                        Course.CourseType.SINGLEWEEK
                                } else if (splitItem.contains("[双]")) {
                                    tCourse.courseType =
                                        Course.CourseType.DOUBLEWEEK
                                } else {
                                    tCourse.courseType =
                                        Course.CourseType.ALL
                                }
                                val location =
                                    splitItem.split(";").get(1)
                                tCourse.location = location
                            }
                            3 -> {//设置教师
                                tCourse.teacher = splitItem
                            }
                            4 -> {//设置周数
                                tCourse.weeks = splitItem
                                tCourseList.add(tCourse.copy())
//                                Log.d(TAG, "parseCourses: 新添加的course信息：${tCourse}")
                            }
                        }
                    }
                }
            }
        }
        mCourseList = tCourseList
//        Log.d(TAG, "parseCourses: 解析完的courseList信息：${tCourseList}")
//        Log.d(TAG, "parseCourses: 解析完的courseList的size信息：${tCourseList.size}")
        return tCourseList
    }

    //非LiveData版本的适配协程域的方法
    fun getStuCourse() = fire_normal(Dispatchers.IO) {
        val response = XierHelperNetwork.getCourseById(mLoginId)
//        LogUtils.d(response.body()?.string())
        val responseBodyHtml = response.body()?.string()
        val document = Jsoup.parse(responseBodyHtml)
        val tableList = document.select("table").get(3).select("tr")
        for (i in tableList.indices) {
            if (i >= 1 && i <= 11) {//选出第1到11行
                val row = tableList.get(i)
                val tds = row.select("td[align='center']")
                val tCourse = Course()
                for (i in 0..7) {
                    val html = tds.get(i).html()
                    val splitedHtml = html.split("<br>")
                    if (i == 0) {
                        tCourse.classOrder = splitedHtml.get(0).toInt()
                        tCourse.classTime = splitedHtml.get(1)
                    } else {
                        tCourse.classDay = i
                        for (i in splitedHtml.indices) {
                            val splitItem = splitedHtml.get(i)
                            when (i % 5) {
                                0 -> {//设置课程名
                                    val formatName =
                                        splitItem.substringAfter(">")
                                            .substringBeforeLast("<")
                                    tCourse.name = formatName
                                }
                                1 -> {
                                    // TODO: 2021/4/26 NULL
                                }
                                2 -> {//设置单双周和教学地点
                                    if (splitItem.contains("[单]")) {
                                        tCourse.courseType =
                                            Course.CourseType.SINGLEWEEK
                                    } else if (splitItem.contains("[双]")) {
                                        tCourse.courseType =
                                            Course.CourseType.DOUBLEWEEK
                                    }
                                    val location =
                                        splitItem.split(";").get(1)
                                    tCourse.location = location
                                }
                                3 -> {//设置教师
                                    tCourse.teacher = splitItem
                                }
                                4 -> {//设置周数
                                    tCourse.weeks = splitItem
                                    mCourseList.add(tCourse)
//                                    println(tCourse)
                                }
                            }
                        }
                    }
                }
            }
        }
//        LogUtils.d(courseList)
        LogUtils.d("退出了")
    }

    //普通网络请求协程接口，适用于请求不常改变的数据的情况，例如用户的登录信息
    private fun fire_normal(context: CoroutineContext, block: suspend () -> Any): Any {
        val job = Job()
        val scope = CoroutineScope(job)
        return scope.async {
            val result = try {
                withContext(context) {
                    block()
                }
            } catch (e: Exception) {
                job.cancel()
                e
            }
        }
    }

    //LiveData网络请求协程接口，适用于经常需要改变的数据，或者MVVM架构应用程序并且涉及到复杂的UI变化，使用到LiveData的情况
    private fun <T> fire_liveData(context: CoroutineContext, block: suspend () -> Result<T>) =
    //这里加的suspend表示所有传入的lambda表达式中的代码拥有挂起函数上下文
        //LiveData{}是一个协程构造方法，会构建一个新协程
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun getCurrentTerm(): String? {
        if (!mCurrentTerm.isEmpty()) {
            return mCurrentTerm
        } else if (isCurrentTermFromFileSaved()) {
            return getCurrentTermFromFile()
        } else {
            return null
        }
    }

    ///////////////////////////////////
    //Room操作

    fun getCourses(): LiveData<List<CourseTableItem>> = mCourseDao.getCourses()

    fun getCourse(id: UUID): LiveData<CourseTableItem?> = mCourseDao.getCourse(id)

    fun updateCourse(course: CourseTableItem) {
        mDaoExecutor.execute {
            mCourseDao.updateCourse(course)
        }
    }

    fun addCourse(course: CourseTableItem) {
        mDaoExecutor.execute {
            mCourseDao.addCourse(course)
        }
    }

    fun addCourses(courses: List<CourseTableItem>) {
        mDaoExecutor.execute {
            mCourseDao.addCourses(courses)
        }
    }

    fun deleteAllCourses() {
        mDaoExecutor.execute {
            mCourseDao.deleteAll()
        }
    }

    ////////////////////////////////
    //sharedPreference操作

    fun getUserFromFile() = UserDao.getSavedUser()?.apply { mLoginId = this.loginId }

    fun getCurrentTermFromFile() =
        UserDao.getSavedCurrentTerm().apply { mCurrentTerm = this!! }

    private fun saveUserToFile(user: User) = UserDao.saveUser(user)

    private fun saveCurrentTermToFile(currentTerm: String) = UserDao.saveCurrentTerm(currentTerm)

    fun isUserFromFileSaved() = UserDao.isUserSaved()

    fun isCurrentTermFromFileSaved() = UserDao.isCurrentTermSaved()

    fun clearUserInfoFromFile() {
        UserDao.clearUserInfo()
        mCurrentTerm = ""
    }
}