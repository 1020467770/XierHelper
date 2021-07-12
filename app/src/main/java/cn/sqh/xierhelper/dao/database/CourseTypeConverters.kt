package cn.sqh.xierhelper.dao.database

import androidx.room.TypeConverter
import cn.sqh.xierhelper.logic.model.Course
import java.util.*

class CourseTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let { Date(it) }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toCourseEnumType(typeIndex: Int) = TYPES[typeIndex]

    @TypeConverter
    fun fromCourseEnumType(enumType: Course.CourseType) = TYPES.indexOf(enumType)

    companion object {
        val TYPES = mutableListOf(
            Course.CourseType.SINGLEWEEK,
            Course.CourseType.DOUBLEWEEK,
            Course.CourseType.ALL
        )
    }


}