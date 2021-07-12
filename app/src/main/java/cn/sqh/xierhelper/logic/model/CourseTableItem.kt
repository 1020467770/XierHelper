package cn.sqh.xierhelper.logic.model

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.ColorUtils
import java.util.*

//项目使用的课程实体类
@Entity
data class CourseTableItem(
    val name: String,//课程名
    var teacher: String = "",//教师
    var weeks: String = "",//始末周数
    val classDay: Int,//星期几的课
    var classOrder: Int,//第几节开始上课
    var classLastTime: Int, //课程节数
    var courseType: Course.CourseType,//根据单双周决定的课程类型
    val location: String, //教室位置
    var itemColor: Int = Color.GRAY,//课程表里的颜色
    val courseStartDate: Date,//课程对应学期开始的日期
    @PrimaryKey
    val id: UUID = UUID.randomUUID()//存入数据库的UUID
) {

//    var term: String? = ""
//    var muser: String? = ""

    constructor(
        course: Course,
        courseStartDate: Date,
        classOrder: Int = 1,
        classLastTime: Int = 1,
    ) : this(
        course.name,
        course.teacher,
        course.weeks,
        course.classDay,
        classOrder,
        classLastTime,
        course.courseType,
        course.location,
        courseStartDate = courseStartDate
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CourseTableItem

        if (name != other.name) return false
        if (teacher != other.teacher) return false
        if (weeks != other.weeks) return false
        if (classDay != other.classDay) return false
        if (courseType != other.courseType) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + teacher.hashCode()
        result = 31 * result + weeks.hashCode()
        result = 31 * result + classDay
        result = 31 * result + courseType.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }
}