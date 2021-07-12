package cn.sqh.xierhelper.core.ui.adapter

import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
import cn.sqh.xierhelper.R
import cn.sqh.xierhelper.logic.model.CustomBean
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class SimpleAdapter : BaseBannerAdapter<CustomBean>() {

    override fun bindData(
        holder: BaseViewHolder<CustomBean>,
        data: CustomBean?,
        position: Int,
        pageSize: Int
    ) {
        holder.setImageResource(R.id.banner_image, data!!.imageRes)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_custom_view;
    }

}
