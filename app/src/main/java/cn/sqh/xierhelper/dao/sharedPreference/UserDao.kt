package cn.sqh.xierhelper.dao.sharedPreference

import android.content.Context
import androidx.core.content.edit
import cn.sqh.xierhelper.XierHelperApplication
import cn.sqh.xierhelper.logic.model.User
import com.google.gson.Gson

private const val DAO_KEY_USER = "user"
private const val DAO_KEY_CURRENT_TERM = "currentTerm"
private const val SHARED_PREFERENCE_NAME = "xier_helper"

object UserDao {

    fun saveUser(user: User) {
        sharedPreferences().edit {
            putString(DAO_KEY_USER, Gson().toJson(user))
        }
    }

    fun getSavedUser(): User? {
        val lastLoginUserJsonStr = sharedPreferences().getString(DAO_KEY_USER, "")
        return Gson().fromJson(lastLoginUserJsonStr, User::class.java)
    }

    fun isUserSaved() = sharedPreferences().contains(DAO_KEY_USER)

    fun saveCurrentTerm(currentTerm: String) {
        sharedPreferences().edit {
            putString(DAO_KEY_CURRENT_TERM, currentTerm)
        }
    }

    fun getSavedCurrentTerm() = sharedPreferences().getString(DAO_KEY_CURRENT_TERM, "")

    fun isCurrentTermSaved() = sharedPreferences().contains(DAO_KEY_CURRENT_TERM)

    fun clearUserInfo() = sharedPreferences().edit { this.clear() }

    private fun sharedPreferences() =
        XierHelperApplication.context.getSharedPreferences(
            SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

}