package cn.sqh.xierhelper.core.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.BaseFragment
import cn.sqh.xierhelper.core.ui.viewModel.CourseViewModel
import cn.sqh.xierhelper.core.ui.viewModel.ToolsViewModel
import cn.sqh.xierhelper.logic.model.Tool
import com.blankj.utilcode.util.ToastUtils

class ToolsFragment : Fragment() {

    val mViewModel: ToolsViewModel by viewModels()

    lateinit var recyclerView: RecyclerView

    private var adapter: ToolAdapter? = ToolAdapter(emptyList())//先用空List初始化adapter，起码保证adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.tools_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this.context, 5)
        recyclerView.adapter = adapter
        return view
    }

    //onViewCreated会在onCreateView之后调用，用到它表明fragment视图层级结果已经准备完毕
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tools = mViewModel.toolItems
        updateUI(tools)
    }

    private fun updateUI(tools: List<Tool>) {
        adapter = ToolAdapter(tools)
        recyclerView.adapter = adapter
    }

    private inner class ToolHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var tool: Tool
        private val toolImageView: ImageView = itemView.findViewById(R.id.iv_tool_img)
        private val toolNameTextView: TextView = itemView.findViewById(R.id.tv_tool_name)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(tool: Tool) {
            this.tool = tool
            toolNameTextView.text = this.tool.name
            toolImageView.setImageResource(this.tool.imgId)
        }

        override fun onClick(v: View?) {
            ToastUtils.showShort("点击了")
//            TODO("Not yet implemented")
        }
    }

    private inner class ToolAdapter(var tools: List<Tool>) : RecyclerView.Adapter<ToolHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolHolder {
            val view = layoutInflater.inflate(R.layout.list_item_tool, parent, false)
            return ToolHolder(view)
        }

        override fun onBindViewHolder(holder: ToolHolder, position: Int) {
            val tool = tools[position]
            holder.bind(tool)
        }

        override fun getItemCount() = tools.size

    }

    companion object {
        fun newInstance() = ToolsFragment()
    }
}