package cn.sqh.xierhelper.core.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.core.ui.fragment.CourseFragment
import com.google.android.material.tabs.TabLayout

class CourseTabAdapter(
    private val fragments: List<CourseFragment>
) : RecyclerView.Adapter<CourseTabAdapter.ViewHolder>() {

    inner class ViewHolder(tabview: View) : RecyclerView.ViewHolder(tabview) {
        val tab: TabLayout = tabview.findViewById(R.id.tab_item_week_num)
        val recyclerView: RecyclerView = tabview.findViewById(R.id.tab_item_day_num)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_course_page, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val fragment = fragments[position]
//        holder.tab = fragment.tab_layout as TabLayout
    }

    override fun getItemCount() = fragments.size

}