package cn.sqh.xierhelper.dao.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.sqh.xierhelper.logic.model.CourseTableItem
import java.util.*

@Dao
interface CourseDao {

    @Query("SELECT * FROM coursetableitem")//对于Query，Room会自动在后台线程上进行查找并返回
    fun getCourses(): LiveData<List<CourseTableItem>>//这里可以用LiveData直接包起来是因为Room和LiveData可自动适配

    @Query("SELECT * FROM coursetableitem WHERE id=(:id)")
    fun getCourse(id: UUID): LiveData<CourseTableItem?>

    //===========================================
    //而对于Insert和Update方法，只能手动在后台线程上执行这些DAO调用

    @Query("DELETE FROM coursetableitem")
    fun deleteAll()

    @Update//自动根据传入的对象ID找到并修改
    fun updateCourse(course: CourseTableItem)

    @Insert
    fun addCourse(course: CourseTableItem)

    @Insert
    fun addCourses(courses: List<CourseTableItem>)
}