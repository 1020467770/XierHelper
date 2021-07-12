package cn.sqh.xierhelper.core

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import cn.sqh.xierhelper.R

//import cn.sqh.xierhelper.databinding.ActivityMainBinding

abstract class ToolbarActivity : BaseActivity() {

    lateinit var mToolbar: Toolbar

    //所有Activity都使用ViewModel
    protected abstract val mViewModel: ViewModel

    protected abstract val mDataBinding: ViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = getLayoutId()

        mToolbar = mDataBinding.root.findViewById(R.id.toolbar)

        mDataBinding.lifecycleOwner = this

        mToolbar.setNavigationOnClickListener {
            finish()//后退键
        }

        setToolbar()
        initView()
    }

    protected fun <T : ViewDataBinding> dataBind() =
        DataBindingUtil.setContentView<T>(this, getLayoutId())

    protected abstract fun initView()

    protected abstract fun setToolbar()

    protected abstract fun getLayoutId(): Int

}