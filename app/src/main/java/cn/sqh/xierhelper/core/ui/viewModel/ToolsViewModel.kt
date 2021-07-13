package cn.sqh.xierhelper.core.ui.viewModel

import androidx.lifecycle.ViewModel
import cn.sqh.xierhelper.dao.ToolRepository

class ToolsViewModel : ViewModel() {

    private val toolReposiroty = ToolRepository

    val toolItems = toolReposiroty.toolItems
}