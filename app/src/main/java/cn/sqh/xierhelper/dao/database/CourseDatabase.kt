package cn.sqh.xierhelper.dao.database

import android.content.Context
import androidx.room.*
import cn.sqh.xierhelper.XierHelperApplication
import cn.sqh.xierhelper.logic.model.CourseTableItem

private const val DATABASE_NAME = "course-database"

@Database(entities = [CourseTableItem::class], version = 1)
@TypeConverters(CourseTypeConverters::class)//注册转换器类
abstract class CourseDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao

    companion object {

        private var instance: CourseDatabase? = null

        @Synchronized
        fun getDatabase(): CourseDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                XierHelperApplication.context.applicationContext,//第一个参数必须使用applicationContext上下文，否则容易出现内存泄漏
                CourseDatabase::class.java,
                DATABASE_NAME
            )
                .build().apply {
                    instance = this
                }
        }
    }

}