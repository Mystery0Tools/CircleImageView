package vip.mystery0.circleimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import kotlin.math.min

class CircleImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
		ImageView(context, attrs, defStyleAttr) {

	private lateinit var viewConfig: CircleImageViewConfig
	private var bitmapWidth = 0
	private var bitmapHeight = 0
	private lateinit var bitmapPaint: Paint//绘制bitmap的画笔
	private lateinit var borderPaint: Paint//绘制边框的画笔
	private var resourceBitmap: Bitmap? = null//存储bitmap

	constructor(context: Context) : this(context, null)
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	init {
		val config = getConfig()
		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
		if (typedArray.hasValue(R.styleable.CircleImageView_draw_circle))
			config.drawCircle = typedArray.getBoolean(R.styleable.CircleImageView_draw_circle, true)
		if (typedArray.hasValue(R.styleable.CircleImageView_draw_border))
			config.drawBorder = typedArray.getBoolean(R.styleable.CircleImageView_draw_border, true)
		if (typedArray.hasValue(R.styleable.CircleImageView_border_width))
			config.borderWidth = typedArray.getDimension(R.styleable.CircleImageView_border_width, config.borderWidth)
		if (typedArray.hasValue(R.styleable.CircleImageView_border_color))
			config.borderColor = typedArray.getColor(R.styleable.CircleImageView_border_color, config.borderColor)
		if (typedArray.hasValue(R.styleable.CircleImageView_circle_radius))
			config.circleRadius = typedArray.getDimension(R.styleable.CircleImageView_circle_radius, config.circleRadius)
		if (typedArray.hasValue(R.styleable.CircleImageView_center_x))
			config.centerX = typedArray.getFloat(R.styleable.CircleImageView_center_x, config.centerX)
		if (typedArray.hasValue(R.styleable.CircleImageView_center_y))
			config.centerY = typedArray.getFloat(R.styleable.CircleImageView_center_y, config.centerY)
		typedArray.recycle()
	}

	@Synchronized
	fun getConfig(): CircleImageViewConfig {
		if (!::viewConfig.isInitialized)
			viewConfig = CircleImageViewConfig()
		return viewConfig
	}

	fun setConfig(config: CircleImageViewConfig): CircleImageView {
		val viewConfig=getConfig()
		this.viewConfig.copy(config)
		return this
	}

	fun config(listener: (CircleImageViewConfig) -> CircleImageViewConfig): CircleImageView =
			setConfig(listener.invoke(getConfig()))

	override fun onDraw(canvas: Canvas) {
		val config=getConfig()
		if (!config.drawCircle)
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
			drawable.setBounds(0, 0, canvas.width, canvas.height)
			drawable.draw(canvas)
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
		val config = getConfig()
		if (config.circleRadius == -1F) {
			config.circleRadius = min(bitmapHeight, bitmapWidth) / 2F
		}
		val bitmapShader = BitmapShader(resourceBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
		bitmapPaint = Paint()
		bitmapPaint.isAntiAlias = true
		bitmapPaint.shader = bitmapShader

		borderPaint = Paint()
		borderPaint.isAntiAlias = true
		borderPaint.style = Paint.Style.STROKE
		borderPaint.color = config.borderColor
		borderPaint.strokeWidth = config.borderWidth
	}

	companion object {
		private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
	}
}