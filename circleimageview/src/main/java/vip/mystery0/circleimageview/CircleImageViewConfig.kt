package vip.mystery0.circleimageview

import android.graphics.Color
import androidx.annotation.ColorInt

class CircleImageViewConfig {
	object Location {
		const val LEFT = 1
		const val TOP = 2
		const val RIGHT = 3
		const val BOTTOM = 4
		const val LEFT_TOP = 5
		const val RIGHT_TOP = 6
		const val LEFT_BOTTOM = 7
		const val RIGHT_BOTTOM = 8
	}

	var drawCircle = true
		//是否裁剪成圆形
		private set
	var drawBorder = true
		//是否绘制边框
		private set
	var drawAdditional = true
		//是否显示额外信息（Pro装饰）
		private set
	var borderWidth = 1F
		//边框宽度
		private set
	var borderColor = Color.BLACK
		//边框颜色
		private set
	var circleRadius = -1F
		//圆形的半径
		private set
	var centerX = 0.5F
		//绘制圆形的圆心X比例，默认是0.5
		private set
	var centerY = 0.5F
		//绘制圆形的圆心Y比例，默认是0.5
		private set

	var additionalLocation = Location.RIGHT_TOP
		private set
	var additionalWidth = 80F
		private set
	var additionalHeight = 40F
		private set
	var additionalMarginHorizontal = 20F
		private set
	var additionalMarginVertical = 20F
		private set

	fun isDrawCircle(isDrawCircle: Boolean): CircleImageViewConfig {
		drawCircle = isDrawCircle
		return this
	}

	fun isDrawBorder(isDrawBorder: Boolean): CircleImageViewConfig {
		drawBorder = isDrawBorder
		return this
	}

	fun isDrawAdditional(isDrawAdditional: Boolean): CircleImageViewConfig {
		this.drawAdditional = isDrawAdditional
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

	fun setCenter(centerX: Float = this.centerX, centerY: Float = this.centerY): CircleImageViewConfig {
		this.centerX = centerX
		if (this.centerX < 0F) this.centerX = 0F
		if (this.centerX > 1F) this.centerX = 1F
		this.centerY = centerY
		if (this.centerY < 0F) this.centerY = 0F
		if (this.centerY > 1F) this.centerY = 1F
		return this
	}

	fun setAdditionalLocation(location: Int): CircleImageViewConfig {
		if (location !in Location.LEFT..Location.RIGHT_BOTTOM)
			throw Exception("input is not a valid value")
		additionalLocation = location
		return this
	}

	fun setAdditionalSize(width: Float = this.additionalWidth, height: Float = this.additionalHeight): CircleImageViewConfig {
		additionalWidth = if (width < 0F) 0F else width
		additionalHeight = if (height < 0F) 0F else height
		return this
	}

	fun setAdditionalMargin(marginHorizontal: Float = this.additionalMarginHorizontal, marginVertical: Float = this.additionalMarginVertical): CircleImageViewConfig {
		additionalMarginHorizontal = marginHorizontal
		additionalMarginVertical = marginVertical
		return this
	}

	fun copy(config: CircleImageViewConfig) {
		isDrawCircle(config.drawCircle)
		isDrawBorder(config.drawBorder)
		isDrawAdditional(config.drawAdditional)
		setBorderWidth(config.borderWidth)
		setBorderColor(config.borderColor)
		setCircleRadius(config.circleRadius)
		setCenter(config.centerX, config.centerY)
		setAdditionalLocation(config.additionalLocation)
		setAdditionalSize(config.additionalWidth, config.additionalHeight)
		setAdditionalMargin(config.additionalMarginHorizontal, config.additionalMarginVertical)
	}
}