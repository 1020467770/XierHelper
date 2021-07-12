package cn.sqh.xierhelper.logic.model

import android.annotation.SuppressLint
import androidx.room.Entity
import java.text.SimpleDateFormat
import java.util.*

//项目使用的课程类，用于扩展选择学期
data class StuCourseInfo(val courses: List<CourseTableItem>, val courseStartDate: Date) {

    @SuppressLint("SimpleDateFormat")
    constructor(courses: List<CourseTableItem>, courseStartDateString: String) : this(
        courses,
        SimpleDateFormat("yyyyMMdd").parse(courseStartDateString) ?: Date()
    )

    constructor(courses: List<CourseTableItem>, courseStartDateInMillion: Long) : this(
        courses,
        Date(courseStartDateInMillion)
    )


}