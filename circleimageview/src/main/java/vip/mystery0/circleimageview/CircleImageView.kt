package vip.mystery0.circleimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min
import android.graphics.RectF


class CircleImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
		ImageView(context, attrs, defStyleAttr) {

	private lateinit var viewConfig: CircleImageViewConfig
	private var bitmapWidth = 0
	private var bitmapHeight = 0
	private lateinit var bitmapPaint: Paint//绘制bitmap的画笔
	private lateinit var borderPaint: Paint//绘制边框的画笔
	private lateinit var additionalPaint: Paint//绘制额外图片的画笔
	private var resourceBitmap: Bitmap? = null//存储bitmap
	private var additionalBitmap: Bitmap? = null//存储额外的图片
	private lateinit var additionalRectF: RectF

	constructor(context: Context) : this(context, null)
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	init {
		val config = getConfig()
		val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
		if (a.hasValue(R.styleable.CircleImageView_draw_circle))
			config.isDrawCircle(a.getBoolean(R.styleable.CircleImageView_draw_circle, config.drawCircle))
		if (a.hasValue(R.styleable.CircleImageView_draw_border))
			config.isDrawBorder(a.getBoolean(R.styleable.CircleImageView_draw_border, config.drawBorder))
		if (a.hasValue(R.styleable.CircleImageView_border_width))
			config.setBorderWidth(a.getDimension(R.styleable.CircleImageView_border_width, config.borderWidth))
		if (a.hasValue(R.styleable.CircleImageView_border_color))
			config.setBorderColor(a.getColor(R.styleable.CircleImageView_border_color, config.borderColor))
		if (a.hasValue(R.styleable.CircleImageView_circle_radius))
			config.setCircleRadius(a.getDimension(R.styleable.CircleImageView_circle_radius, config.circleRadius))
		if (a.hasValue(R.styleable.CircleImageView_center_x))
			config.setCenter(centerX = a.getFloat(R.styleable.CircleImageView_center_x, config.centerX))
		if (a.hasValue(R.styleable.CircleImageView_center_y))
			config.setCenter(centerY = a.getFloat(R.styleable.CircleImageView_center_y, config.centerY))
		if (a.hasValue(R.styleable.CircleImageView_additional_src))
			setAdditionalImageDrawable(a.getDrawable(R.styleable.CircleImageView_additional_src))
		if (a.hasValue(R.styleable.CircleImageView_additional_location))
			config.setAdditionalLocation(a.getInteger(R.styleable.CircleImageView_additional_location, config.additionalLocation))
		if (a.hasValue(R.styleable.CircleImageView_additional_width))
			config.setAdditionalSize(width = a.getDimension(R.styleable.CircleImageView_additional_width, config.additionalWidth))
		if (a.hasValue(R.styleable.CircleImageView_additional_height))
			config.setAdditionalSize(height = a.getDimension(R.styleable.CircleImageView_additional_height, config.additionalHeight))
		if (a.hasValue(R.styleable.CircleImageView_additional_margin_horizontal))
			config.setAdditionalMargin(marginHorizontal = a.getDimension(R.styleable.CircleImageView_additional_margin_horizontal, config.additionalMarginHorizontal))
		if (a.hasValue(R.styleable.CircleImageView_additional_margin_vertical))
			config.setAdditionalMargin(marginVertical = a.getDimension(R.styleable.CircleImageView_additional_margin_vertical, config.additionalMarginVertical))
		a.recycle()
		updateConfig()
	}

	@Synchronized
	private fun getConfig(): CircleImageViewConfig {
		if (!::viewConfig.isInitialized)
			viewConfig = CircleImageViewConfig()
		return viewConfig
	}

	fun setConfig(config: CircleImageViewConfig): CircleImageView {
		getConfig().copy(config)
		updateConfig()
		return this
	}

	fun config(listener: (CircleImageViewConfig) -> CircleImageViewConfig): CircleImageView =
			setConfig(listener.invoke(getConfig()))

	private fun updateConfig() {
		val config = getConfig()
		updatePaintConfig(config)
		updateAdditionalConfig(config)
		invalidate()
	}

	private fun updatePaintConfig(config: CircleImageViewConfig) {
		if (config.circleRadius == -1F) {
			config.setCircleRadius(min(bitmapHeight, bitmapWidth) / 2F)
		}
		borderPaint.color = config.borderColor
		borderPaint.strokeWidth = config.borderWidth
	}

	private fun updateAdditionalConfig(config: CircleImageViewConfig) {
		if (additionalBitmap == null)
			return
		config.setAdditionalSize(width = max(additionalBitmap!!.width.toFloat(), config.additionalWidth), height = max(additionalBitmap!!.height.toFloat(), config.additionalHeight))
		val marginRectF = RectF(
				config.additionalMarginHorizontal,
				config.additionalMarginVertical,
				bitmapWidth - config.additionalMarginHorizontal,
				bitmapHeight - config.additionalMarginVertical)
		additionalRectF = when (config.additionalLocation) {
			CircleImageViewConfig.Location.LEFT -> RectF(
					marginRectF.left,
					(marginRectF.top + marginRectF.bottom - config.additionalHeight) / 2,
					marginRectF.left + config.additionalWidth,
					(marginRectF.top + marginRectF.bottom + config.additionalHeight) / 2)
			CircleImageViewConfig.Location.TOP -> RectF(
					(marginRectF.left + marginRectF.right - config.additionalWidth) / 2,
					marginRectF.top,
					(marginRectF.left + marginRectF.right + config.additionalWidth) / 2,
					marginRectF.top + config.additionalHeight)
			CircleImageViewConfig.Location.RIGHT -> RectF(
					marginRectF.right - config.additionalWidth,
					(marginRectF.top + marginRectF.bottom - config.additionalHeight) / 2,
					marginRectF.right,
					(marginRectF.top + marginRectF.bottom + config.additionalHeight) / 2)
			CircleImageViewConfig.Location.BOTTOM -> RectF(
					(marginRectF.left + marginRectF.right - config.additionalWidth) / 2,
					marginRectF.bottom - config.additionalHeight,
					(marginRectF.left + marginRectF.right + config.additionalWidth) / 2,
					marginRectF.bottom)
			CircleImageViewConfig.Location.LEFT_TOP -> RectF(
					marginRectF.left,
					marginRectF.top,
					marginRectF.left + config.additionalWidth,
					marginRectF.top + config.additionalHeight)
			CircleImageViewConfig.Location.RIGHT_TOP -> RectF(
					marginRectF.right - config.additionalWidth,
					marginRectF.top,
					marginRectF.right,
					marginRectF.top + config.additionalHeight)
			CircleImageViewConfig.Location.LEFT_BOTTOM -> RectF(
					marginRectF.left,
					marginRectF.bottom - config.additionalHeight,
					marginRectF.left + config.additionalWidth,
					marginRectF.bottom)
			CircleImageViewConfig.Location.RIGHT_BOTTOM -> RectF(
					marginRectF.right - config.additionalWidth,
					marginRectF.bottom - config.additionalHeight,
					marginRectF.right,
					marginRectF.bottom)
			else -> throw Exception("The value of additional location is error")
		}
	}

	override fun onDraw(canvas: Canvas) {
		val config = getConfig()
		if (!config.drawCircle && !config.drawAdditional)
			return super.onDraw(canvas)
		canvas.drawCircle(bitmapWidth * config.centerX, bitmapHeight * config.centerY, config.circleRadius, bitmapPaint)
		if (config.drawBorder) {//绘制边框
			canvas.drawCircle(
					bitmapWidth * config.centerX,
					bitmapHeight * config.centerY,
					config.circleRadius - config.borderWidth / 2F,
					borderPaint
			)
		}
		if (config.drawAdditional && additionalBitmap != null)
			canvas.drawBitmap(additionalBitmap!!, null, additionalRectF, additionalPaint)
	}

	fun setAdditionImageResource(resId: Int) = setAdditionalImageDrawable(ContextCompat.getDrawable(context, resId))

	fun setAdditionalImageDrawable(drawable: Drawable?) {
		if (drawable == null)
			return
		setAdditionalImageBitmap(drawable.toBitmap())
	}

	fun setAdditionalImageBitmap(bitmap: Bitmap) {
		additionalBitmap = bitmap
		additionalPaint = Paint()
		additionalPaint.isAntiAlias = true
		additionalPaint.style = Paint.Style.FILL
		updateConfig()
	}

	/**
	 * 拦截设置图片Bitmap或Drawable
	 * setImageBitmap最终会调用setImageDrawable
	 */
	override fun setImageDrawable(drawable: Drawable?) {
		super.setImageDrawable(drawable)
		initBitmapAndPaint()
	}

	/**
	 * 将Drawable转换成Bitmap
	 */
	private fun Drawable.toBitmap(): Bitmap {
		val bitmap = when (this) {
			is BitmapDrawable -> this.bitmap
			is ColorDrawable -> Bitmap.createBitmap(2, 2, BITMAP_CONFIG)
			else -> Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, BITMAP_CONFIG)
		}
		if (this !is BitmapDrawable) {//如果不是BitmapDrawable，需要将drawable画到bitmap上面
			val canvas = Canvas(bitmap)
			this.setBounds(0, 0, canvas.width, canvas.height)
			this.draw(canvas)
		}
		return bitmap
	}

	/**
	 * 根据bitmap来初始化配置项
	 */
	private fun initBitmapAndPaint() {
		resourceBitmap = drawable.toBitmap()
		bitmapHeight = resourceBitmap!!.height
		bitmapWidth = resourceBitmap!!.width

		val bitmapShader = BitmapShader(resourceBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
		bitmapPaint = Paint()
		bitmapPaint.isAntiAlias = true
		bitmapPaint.shader = bitmapShader

		borderPaint = Paint()
		borderPaint.isAntiAlias = true
		borderPaint.style = Paint.Style.STROKE
	}

	companion object {
		private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
	}
}