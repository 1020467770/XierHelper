package cn.sqh.xierhelper.core.ui.customView

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View.OnTouchListener
import com.blankj.utilcode.util.LogUtils


//自定义按钮，可以设置颜色圆角等
class ButtonM(context: Context?, attrs: AttributeSet?, defStyle: Int) :
    androidx.appcompat.widget.AppCompatButton(context, attrs, defStyle) {
    private var gradientDrawable //控件的样式
            : GradientDrawable? = null
    private var backColors = "" //背景色，String类型
    private var backColori = 0 //背景色，int类型
    private var backColorSelecteds = "" //按下后的背景色，String类型
    private var backColorSelectedi = 0 //按下后的背景色，int类型
    private var backGroundImage = 0 //背景图，只提供了Id
    private var backGroundImageSeleted = 0 //按下后的背景图，只提供了Id
    private var textColors = "" //文字颜色，String类型
    private var textColori = 0 //文字颜色，int类型
    private var textColorSeleteds = "" //按下后的文字颜色，String类型
    private var textColorSeletedi = 0 //按下后的文字颜色，int类型
    private val radius = 8f //圆角半径
    private var shape = 0 //圆角样式，矩形、圆形等，由于矩形的Id为0，默认为矩形
    private var fillet = false //是否设置圆角
    private var isCost = true

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {}
    constructor(context: Context?) : this(context, null) {}

    private fun init() {
        //将Button的默认背景色改为透明，本人不喜欢原来的颜色
        if (fillet) {
            if (gradientDrawable == null) {
                gradientDrawable = GradientDrawable()
            }
            gradientDrawable!!.setColor(Color.TRANSPARENT)
        } else {
            setBackgroundColor(Color.TRANSPARENT)
        }
        //设置文字默认居中
        setGravity(Gravity.CENTER)
        //设置Touch事件
        setOnTouchListener(OnTouchListener { arg0, event -> //按下改变样式
            setColor(event.action)
            //此处设置为false，防止Click事件被屏蔽
        })
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        isCost = false
    }

    //改变样式
    private fun setColor(state: Int): Boolean {
        if (state == MotionEvent.ACTION_DOWN) {
//            LogUtils.d("按下去了")
            //按下
            if (backColorSelectedi != 0) {
                //先判断是否设置了按下后的背景色int型
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = GradientDrawable()
                    }
                    gradientDrawable!!.setColor(backColorSelectedi)
                } else {
                    setBackgroundColor(backColorSelectedi)
                }
            } else if (backColorSelecteds != "") {
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = GradientDrawable()
                    }
                    gradientDrawable!!.setColor(Color.parseColor(backColorSelecteds))
                } else {
                    setBackgroundColor(Color.parseColor(backColorSelecteds))
                }
            }
            //判断是否设置了按下后文字的颜色
            if (textColorSeletedi != 0) {
                setTextColor(textColorSeletedi)
            } else if (textColorSeleteds != "") {
                setTextColor(Color.parseColor(textColorSeleteds))
            }
            //判断是否设置了按下后的背景图
            if (backGroundImageSeleted != 0) {
                setBackgroundResource(backGroundImageSeleted)
            }
        }
        if (state == MotionEvent.ACTION_UP) {
//            LogUtils.d("抬起来了")
            //抬起
            if (backColori == 0 && backColors == "") {
                //如果没有设置背景色，默认改为透明
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = GradientDrawable()
                    }
                    gradientDrawable!!.setColor(Color.TRANSPARENT)
                } else {
                    setBackgroundColor(Color.TRANSPARENT)
                }
            } else if (backColori != 0) {
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = GradientDrawable()
                    }
                    gradientDrawable!!.setColor(backColori)
                } else {
                    setBackgroundColor(backColori)
                }
            } else {
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = GradientDrawable()
                    }
                    gradientDrawable!!.setColor(Color.parseColor(backColors))
                } else {
                    setBackgroundColor(Color.parseColor(backColors))
                }
            }
            //如果为设置字体颜色，默认为黑色
            if (textColori == 0 && textColors == "") {
                setTextColor(Color.BLACK)
            } else if (textColori != 0) {
                setTextColor(textColori)
            } else {
                setTextColor(Color.parseColor(textColors))
            }
            if (backGroundImage != 0) {
                setBackgroundResource(backGroundImage)
            }
        }
        return isCost
    }

    /**
     * 设置按钮的背景色,如果未设置则默认为透明
     * @param backColor
     */
    fun setBackColor(backColor: String) {
        backColors = backColor
        if (backColor == "") {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = GradientDrawable()
                }
                gradientDrawable!!.setColor(Color.TRANSPARENT)
            } else {
                setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = GradientDrawable()
                }
                gradientDrawable!!.setColor(Color.parseColor(backColor))
            } else {
                setBackgroundColor(Color.parseColor(backColor))
            }
        }
    }

    /**
     * 设置按钮的背景色,如果未设置则默认为透明
     * @param backColor
     */
    fun setBackColor(backColor: Int) {
        backColori = backColor
        if (backColori == 0) {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = GradientDrawable()
                }
                gradientDrawable!!.setColor(Color.TRANSPARENT)
            } else {
                setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = GradientDrawable()
                }
                gradientDrawable!!.setColor(backColor)
            } else {
                setBackgroundColor(backColor)
            }
        }
    }

    /**
     * 设置按钮按下后的颜色
     * @param backColorSelected
     */
    fun setBackColorSelected(backColorSelected: Int) {
        backColorSelectedi = backColorSelected
    }

    /**
     * 设置按钮按下后的颜色
     * @param backColorSelected
     */
    fun setBackColorSelected(backColorSelected: String) {
        backColorSelecteds = backColorSelected
    }

    /**
     * 设置按钮的背景图
     * @param backGroundImage
     */
    fun setBackGroundImage(backGroundImage: Int) {
        this.backGroundImage = backGroundImage
        if (backGroundImage != 0) {
            setBackgroundResource(backGroundImage)
        }
    }

    /**
     * 设置按钮按下的背景图
     * @param backGroundImageSeleted
     */
    fun setBackGroundImageSeleted(backGroundImageSeleted: Int) {
        this.backGroundImageSeleted = backGroundImageSeleted
    }

    /**
     * 设置按钮圆角半径大小
     * @param radius
     */
    fun setRadius(radius: Float) {
        if (gradientDrawable == null) {
            gradientDrawable = GradientDrawable()
        }
        gradientDrawable!!.cornerRadius = radius
    }

    /**
     * 设置按钮文字颜色
     * @param textColor
     */
    fun setTextColors(textColor: String) {
        textColors = textColor
        setTextColor(Color.parseColor(textColor))
    }

    /**
     * 设置按钮文字颜色
     * @param textColor
     */
    fun setTextColori(textColor: Int) {
        textColori = textColor
        setTextColor(textColor)
    }

    /**
     * 设置按钮按下的文字颜色
     * @param textColor
     */
    fun setTextColorSelected(textColor: String) {
        textColorSeleteds = textColor
    }

    /**
     * 设置按钮按下的文字颜色
     * @param textColor
     */
    fun setTextColorSelected(textColor: Int) {
        textColorSeletedi = textColor
    }

    /**
     * 按钮的形状
     * @param shape
     */
    fun setShape(shape: Int) {
        this.shape = shape
    }

    /**
     * 设置其是否为圆角
     * @param fillet
     */
    fun setFillet(fillet: Boolean) {
        this.fillet = fillet
        if (fillet) {
            if (gradientDrawable == null) {
                gradientDrawable = GradientDrawable()
            }
            //GradientDrawable.RECTANGLE
            gradientDrawable!!.shape = shape
            gradientDrawable!!.cornerRadius = radius
            setBackground(gradientDrawable)
        }
    }

    init {
        init()
    }
}
