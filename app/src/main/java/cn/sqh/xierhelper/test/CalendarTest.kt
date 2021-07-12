package cn.sqh.xierhelper.test

import java.text.SimpleDateFormat
import java.util.*


fun main() {
    val calendar = GregorianCalendar(2021, 2, 1)
//    Calendar
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    println(dateFormat.format(calendar.time))
    println("月份是${calendar.get(Calendar.MONTH)}")
//    calendar.add(Calendar.DAY_OF_WEEK_IN_MONTH, 21)
    calendar.add(Calendar.DAY_OF_YEAR, 103)
    println(dateFormat.format(calendar.time))

//    println(calendar.get(Calendar.DAY_OF_WEEK))
//    println(calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH))
//    println(calendar.get(Calendar.WEEK_OF_MONTH))
//    println(calendar.get(Calendar.WEEK_OF_YEAR))

}