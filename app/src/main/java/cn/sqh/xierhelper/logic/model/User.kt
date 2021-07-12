package cn.sqh.xierhelper.logic.model

//登录用的User
data class User(val muser: String, val password: String, var loginId: String = "") {
}