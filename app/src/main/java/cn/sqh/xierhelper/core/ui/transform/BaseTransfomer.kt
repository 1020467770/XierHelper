package cn.sqh.xierhelper.core.ui.transform

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * 由github某创作者提供
 */
abstract class BaseTransformer : ViewPager2.PageTransformer {

    protected abstract fun onTransform(page: View?, position: Float)

    override fun transformPage(page: View, position: Float) {
        onPreTransform(page, position)
        onTransform(page, position)
        onPostTransform(page, position)
    }

    protected fun hideOffscreenPages(): Boolean {
        return true
    }

    protected open fun isPagingEnabled(): Boolean {
        return false
    }


    protected fun onPreTransform(page: View, position: Float) {
        val width = page.width.toFloat()
        page.rotationX = 0f
        page.rotationY = 0f
        page.rotation = 0f
        page.scaleX = 1f
        page.scaleY = 1f
        page.pivotX = 0f
        page.pivotY = 0f
        page.translationY = 0f
        page.translationX = if (isPagingEnabled()) 0f else -width * position
        if (hideOffscreenPages()) {
            page.alpha = if (position <= -1f || position >= 1f) 0f else 1f
            //			page.setEnabled(false);
        } else {
//			page.setEnabled(true);
            page.alpha = 1f
        }
    }

    protected fun onPostTransform(page: View?, position: Float) {}

    companion object {

        protected fun min(value: Float, min: Float): Float {
            return if (value < min) min else value
        }
    }
}
