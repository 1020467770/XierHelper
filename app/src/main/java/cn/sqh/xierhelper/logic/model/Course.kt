package cn.sqh.xierhelper.logic.model

//从html文件中解析出的教务处课程类
data class Course(
    var name: String = "",//课程名
    var teacher: String = "",//教师
    var weeks: String = "",//始末周数
    var classDay: Int = 0,//星期几上课
    var classOrder: Int = 0,//第几节的课
    var classTime: String = "",//什么时间上课
    var courseType: CourseType = CourseType.ALL,//根据单双周决定的课程类型
    var location: String = ""//上课教室
) {
    enum class CourseType {
        ALL,
        SINGLEWEEK,
        DOUBLEWEEK
    }

}