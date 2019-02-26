package vip.mystery0.circleimageview

import android.graphics.Color
import androidx.annotation.ColorInt

class CircleImageViewConfig {
    var drawCircle = true//是否裁剪成圆形
    var drawBorder = true//是否绘制边框
    var borderWidth = 1F//边框宽度
    var borderColor = Color.BLACK//边框颜色
    var circleRadius = -1F//圆形的半径
    var centerX = 0.5F//绘制圆形的圆心X比例，默认是0.5
    var centerY = 0.5F//绘制圆形的圆心Y比例，默认是0.5

    var isShowAdditional=false//是否显示额外信息（Pro装饰）

    fun isDrawCircle(isDrawCircle: Boolean): CircleImageViewConfig {
        drawCircle = isDrawCircle
        return this
    }

    fun isDrawBorder(isDrawBorder: Boolean): CircleImageViewConfig {
        drawBorder = isDrawBorder
        return this
    }

    fun setBorderWidth(borderWidth: Float): CircleImageViewConfig {
        if (borderWidth < 0F)
            throw RuntimeException("border width cannot be less than zero.")
        this.borderWidth = borderWidth
        return this
    }

    fun setBorderColor(@ColorInt borderColor: Int): CircleImageViewConfig {
        this.borderColor = borderColor
        return this
    }

    fun setCircleRadius(circleRadius: Float): CircleImageViewConfig {
        if (circleRadius < 0F)
            throw RuntimeException("circle radius cannot be less than zero.")
        this.circleRadius = circleRadius
        return this
    }

    fun setCenterX(centerX: Float): CircleImageViewConfig {
        this.centerX = centerX
        if (this.centerX < 0F) this.centerX = 0F
        if (this.centerX > 1F) this.centerX = 1F
        return this
    }

    fun setCenterY(centerY: Float): CircleImageViewConfig {
        this.centerY = centerY
        if (this.centerY < 0F) this.centerY = 0F
        if (this.centerY > 1F) this.centerY = 1F
        return this
    }

    fun copy(config: CircleImageViewConfig) {
        isDrawCircle(config.drawCircle)
        isDrawBorder(config.drawBorder)
        setBorderWidth(config.borderWidth)
        setBorderColor(config.borderColor)
        setCircleRadius(config.circleRadius)
        setCenterX(config.centerX)
        setCenterY(config.centerY)
    }
}