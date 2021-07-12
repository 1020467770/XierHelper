package cn.sqh.xierhelper.core.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.sqh.xierhelper.dao.Repository
import cn.sqh.xierhelper.logic.model.CourseTableItem
import cn.sqh.xierhelper.logic.model.StuCourseInfo
import cn.sqh.xierhelper.logic.network.OkHttpManager
import com.blankj.utilcode.util.LogUtils
import com.franmontiel.persistentcookiejar.ClearableCookieJar

class CourseViewModel : ViewModel() {

    //根据下拉栏选择的termString改变LiveData的值
    private val _selectedTermString = MutableLiveData<String>()

    fun getCurrentTerm() = Repository.getCurrentTerm()

    //尝试通过网络获取CourseList
    val courseLiveData = Transformations.switchMap(_selectedTermString) { term ->
        Repository.getStuCourse(term)
    }

    //根据选择的学期获取CourseList
    fun getCoursesByTerm(term: String) {
        _selectedTermString.value = term
    }

    //默认获取当前学期
    fun getCoursesByTerm() {
        val currentTerm = getCurrentTerm()
        currentTerm?.let {
            getCoursesByTerm(currentTerm)
        }
    }

    fun setStuCourseInfo(courses: List<CourseTableItem>) {
        Repository.currentTermCourseInfo = StuCourseInfo(courses, courses.get(0).courseStartDate)
    }

    //获取当前学期课程信息
    fun getStuCourseInfo() = Repository.currentTermCourseInfo

    //保存当前设备侧栏高度
    fun saveTvMeasuredHeight(height: Int) {
        Repository.leftTvMeasuredHeight = height
    }

    //当前设备侧栏高度是否储存
    fun isSavedTvMeasuredHeight() = Repository.leftTvMeasuredHeight != null

    //获取当前设备侧栏高度
    fun getTvMeasuredHeight() = Repository.leftTvMeasuredHeight

    //尝试从数据库里获取CourseList
    val courseListLiveDateFromLocal = Repository.getCourses()

    fun addCourse(course: CourseTableItem) {
        Repository.addCourse(course)
    }

    fun addCourse(courses: List<CourseTableItem>) {
        Repository.addCourses(courses)
    }

    //清除登录保存的信息
    fun clearLoginInfo() {
        //清除User信息
        Repository.clearUserInfoFromFile()
        //清除User的课程
        Repository.deleteAllCourses()
        //清除登录的Cookie信息
        (OkHttpManager.okHttpClient.cookieJar() as ClearableCookieJar).clear()
    }

    //重新加载课程进数据库
    fun refreshAllCourses(courses: List<CourseTableItem>) {
        Repository.deleteAllCourses()
        Repository.addCourses(courses)
    }

    fun refreshUserInfoIntoMemory() {
        Repository.getUserFromFile()
        Repository.getCurrentTermFromFile()
    }


}