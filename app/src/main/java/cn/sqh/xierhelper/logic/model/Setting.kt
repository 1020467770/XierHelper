package cn.sqh.xierhelper.logic.model


data class Setting(var datas: HashMap<Int, Any> = HashMap()) {

    /**
     * 设置的初始化
     */
    init {
        datas[SETTING_TYPE_IS_CHANGE_PAGE_SMOOTH] = true
        datas[SETTING_TYPE_IS_CHANGE_PAGE_SCALE] = false
    }

    companion object {
        const val SETTING_TYPE_IS_CHANGE_PAGE_SMOOTH = 0
        const val SETTING_TYPE_IS_CHANGE_PAGE_SCALE = 1
    }

}