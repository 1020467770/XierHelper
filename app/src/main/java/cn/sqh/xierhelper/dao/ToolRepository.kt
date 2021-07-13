package cn.sqh.xierhelper.dao

import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.logic.model.Tool

object ToolRepository {

    val toolItems: List<Tool> = listOf(
        Tool("学业状况", R.drawable.ic_study),
        Tool("历年卷", R.drawable.ic_study),
        Tool("空教室", R.drawable.ic_study),
        Tool("考场查询", R.drawable.ic_study),
        Tool("一键评议", R.drawable.ic_study),
        Tool("图书馆", R.drawable.ic_study),
        Tool("嘉熙讲坛", R.drawable.ic_study),
        )
}