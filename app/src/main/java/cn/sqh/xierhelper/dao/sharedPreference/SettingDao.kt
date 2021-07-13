package cn.sqh.xierhelper.dao.sharedPreference

import android.content.Context
import androidx.core.content.edit
import cn.sqh.xierhelper.XierHelperApplication
import cn.sqh.xierhelper.logic.model.Setting
import com.google.gson.Gson


private const val DAO_KEY_SETTING = "settings"
private const val SHARED_PREFERENCE_NAME = "xier_helper"

object SettingDao {

    fun saveSetting(setting: Setting) {
        sharedPreferences().edit {
            putString(DAO_KEY_SETTING, Gson().toJson(setting))
        }
    }

    fun getSetting() =
        Gson().fromJson(sharedPreferences().getString(DAO_KEY_SETTING, ""), Setting::class.java)

    fun isSettingSaved() = sharedPreferences().contains(DAO_KEY_SETTING)

    private fun sharedPreferences() =
        XierHelperApplication.context.getSharedPreferences(
            SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
}