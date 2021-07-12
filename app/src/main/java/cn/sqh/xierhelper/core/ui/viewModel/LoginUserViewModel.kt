package cn.sqh.xierhelper.core.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.sqh.xierhelper.dao.Repository
import cn.sqh.xierhelper.logic.model.User
import com.blankj.utilcode.util.LogUtils

class LoginUserViewModel : ViewModel() {

    private val _loggingUser = MutableLiveData<User>()

    var loginMuser: String? = ""
    var loginPaswd: String? = ""
    var loginVerifyCode: String? = ""

    val loginUserLiveData = Transformations.switchMap(_loggingUser) { user ->
        loginVerifyCode?.let { Repository.loginWithLiveData(user, it) }
    }

    fun login() {
        if (!loginMuser.isNullOrBlank() && !loginPaswd.isNullOrBlank()) {
            _loggingUser.value = User(loginMuser!!, loginPaswd!!)
        }
    }


}