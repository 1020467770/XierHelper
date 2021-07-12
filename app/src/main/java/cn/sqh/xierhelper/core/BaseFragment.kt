package cn.sqh.xierhelper.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * 这个自定义的Fragment基类实现了DataBinding的初始化和销毁操作
 * 继承自BaseFragment的Fragment都可实现DataBinding，无需考虑DataBinding的销毁操作
 */
abstract class BaseFragment : Fragment() {

    protected var _binding: ViewDataBinding? = null

    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getDataBinding(inflater, container)
        initView(inflater, container, savedInstanceState)
        return binding.root
    }

    abstract fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )

    abstract fun getDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ViewDataBinding?

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}