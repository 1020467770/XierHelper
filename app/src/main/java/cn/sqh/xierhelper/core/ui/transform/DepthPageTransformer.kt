package cn.sqh.xierhelper.core.ui.transform

import android.view.View

/**
 * 由Github某创作者提供
 * 给Banner增加Transformer动画
 */
class DepthPageTransformer : BaseTransformer() {
    override fun onTransform(view: View?, position: Float) {
        if (position <= 0f) {
            view?.let {
                it.translationX = 0f
                it.scaleX = 1f
                it.scaleY = 1f
            }
        } else if (position <= 1f) {
            val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
            view?.let {
                it.alpha = 1 - position
                it.pivotY = 0.5f * it.height
                it.translationX = it.width * -position
                it.scaleX = scaleFactor
                it.scaleY = scaleFactor
            }
        }
    }

    override fun isPagingEnabled(): Boolean {
        return true
    }

    companion object {
        private const val MIN_SCALE = 0.75f
    }
}