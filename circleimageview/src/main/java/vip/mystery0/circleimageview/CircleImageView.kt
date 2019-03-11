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
import android.net.Uri

class CircleImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
		ImageView(context, attrs, defStyleAttr) {
	private val config by lazy { CircleImageViewConfig() }
	private var bitmapWidth = 0
	private var bitmapHeight = 0
	private val bitmapPaint by lazy { Paint() }//绘制bitmap的画笔
	private val borderPaint by lazy { Paint() }//绘制边框的画笔
	private val additionalPaint by lazy { Paint() }//绘制额外图片的画笔
	private var resourceBitmap: Bitmap? = null//存储bitmap
	private var additionalBitmap: Bitmap? = null//存储额外的图片
	private lateinit var additionalRectF: RectF
	private val drawableRect by lazy { RectF() }
	private val borderRect by lazy { RectF() }
	private lateinit var bitmapShader: BitmapShader
	private val shaderMatrix by lazy { Matrix() }
	private var isReady = false
	private var isSetupPending = false

	constructor(context: Context) : this(context, null)
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	init {
		if (attrs != null) {
			val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
			if (a.hasValue(R.styleable.CircleImageView_draw_circle))
				config.isDrawCircle(a.getBoolean(R.styleable.CircleImageView_draw_circle, config.drawCircle))
			if (a.hasValue(R.styleable.CircleImageView_draw_border))
				config.isDrawBorder(a.getBoolean(R.styleable.CircleImageView_draw_border, config.drawBorder))
			if (a.hasValue(R.styleable.CircleImageView_draw_additional))
				config.isDrawAdditional(a.getBoolean(R.styleable.CircleImageView_draw_additional, config.drawAdditional))
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
		}
		init()
	}

	fun setConfig(config: CircleImageViewConfig): CircleImageView {
		this.config.copy(config)
		init()
		return this
	}

	fun config(listener: (CircleImageViewConfig) -> CircleImageViewConfig): CircleImageView =
			setConfig(listener.invoke(config))

	private fun init() {
		super.setScaleType(SCALE_TYPE)
		isReady = true
		if (isSetupPending) {
			setup()
			isSetupPending = false
		}
	}

	override fun getScaleType(): ScaleType = SCALE_TYPE

	/**
	 * 设置相关数据
	 */
	private fun setup() {
		if (!isReady) {
			isSetupPending = true
			return
		}
		if (resourceBitmap == null)
			return
		initPaint()
	}

	override fun onDraw(canvas: Canvas) {
		if (drawable == null)
			return
		if ((!config.drawCircle) && (!config.drawBorder) && (!config.drawAdditional))
			return super.onDraw(canvas)
		canvas.drawCircle(width * config.centerX, height * config.centerY, config.circleRadius, bitmapPaint)
		if (config.drawBorder && config.borderWidth != 0F)
			canvas.drawCircle(width * config.centerX, height * config.centerY, config.borderRadius, borderPaint)

		if (config.drawAdditional && additionalBitmap != null) {
			canvas.drawBitmap(additionalBitmap!!, null, additionalRectF, additionalPaint)
		}
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		setup()
	}

	override fun setColorFilter(cf: ColorFilter?) {
		if (cf == null)
			return
		bitmapPaint.colorFilter = cf
		invalidate()
	}

	/**
	 * 设置额外图片
	 */
	fun setAdditionImageResource(resId: Int) = setAdditionalImageDrawable(ContextCompat.getDrawable(context, resId))

	/**
	 * 设置额外图片
	 */
	fun setAdditionalImageDrawable(drawable: Drawable?) {
		if (drawable == null)
			return
		setAdditionalImageBitmap(drawable.toBitmap())
	}

	/**
	 * 设置额外图片
	 */
	fun setAdditionalImageBitmap(bitmap: Bitmap) {
		additionalBitmap = bitmap
		setup()
	}

	override fun setImageResource(resId: Int) {
		super.setImageResource(resId)
		resourceBitmap = drawable.toBitmap()
		setup()
	}

	override fun setImageBitmap(bm: Bitmap?) {
		super.setImageBitmap(bm)
		resourceBitmap = bm
		setup()
	}

	override fun setImageURI(uri: Uri?) {
		super.setImageURI(uri)
		resourceBitmap = drawable.toBitmap()
		setup()
	}

	/**
	 * 拦截设置图片Bitmap或Drawable
	 * setImageBitmap最终会调用setImageDrawable
	 */
	override fun setImageDrawable(drawable: Drawable?) {
		super.setImageDrawable(drawable)
		resourceBitmap = drawable?.toBitmap()
		setup()
	}

	/**
	 * 根据bitmap来初始化配置项
	 */
	private fun initPaint() {
		//初始化着色器
		bitmapShader = BitmapShader(resourceBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
		bitmapPaint.isAntiAlias = true
		bitmapPaint.shader = bitmapShader

		//初始化边框画笔
		borderPaint.isAntiAlias = true
		borderPaint.style = Paint.Style.STROKE
		borderPaint.color = config.borderColor
		borderPaint.strokeWidth = config.borderWidth

		bitmapHeight = resourceBitmap!!.height
		bitmapWidth = resourceBitmap!!.width

		if (additionalBitmap != null) {
			additionalPaint.isAntiAlias = true
			additionalPaint.style = Paint.Style.FILL
			setAdditionalConfig()
		}

		borderRect.set(0F, 0F, width.toFloat(), height.toFloat())
		config.setBorderRadius(min(borderRect.height() - config.borderWidth, borderRect.width() - config.borderWidth) / 2F)
		drawableRect.set(config.borderWidth, config.borderWidth, borderRect.width() - config.borderWidth, borderRect.height() - config.borderWidth)
		config.setCircleRadius(min(drawableRect.height(), drawableRect.width()) / 2F)

		updateShaderMatrix()
		invalidate()
	}

	/**
	 * 设置额外图片的配置参数
	 */
	private fun setAdditionalConfig() {
		if (additionalBitmap == null)
			return
		config.setAdditionalSize(width = max(additionalBitmap!!.width.toFloat(), config.additionalWidth), height = max(additionalBitmap!!.height.toFloat(), config.additionalHeight))
		val marginRectF = RectF(
				config.additionalMarginHorizontal,
				config.additionalMarginVertical,
				width - config.additionalMarginHorizontal,
				height - config.additionalMarginVertical)
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

	/**
	 * 更新Shader的缩放配置
	 */
	private fun updateShaderMatrix() {
		val scale: Float
		var dx = 0F
		var dy = 0F

		shaderMatrix.set(null)

		if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
			scale = drawableRect.height() / bitmapHeight.toFloat()
			dx = (drawableRect.width() - bitmapWidth * scale) * 0.5F
		} else {
			scale = drawableRect.width() / bitmapWidth.toFloat()
			dy = (drawableRect.height() - bitmapHeight * scale) * 0.5F
		}
		shaderMatrix.setScale(scale, scale)
		shaderMatrix.postTranslate((dx + 0.5F).toInt() + config.borderWidth, (dy + 0.5F) + config.borderWidth)
		bitmapShader.setLocalMatrix(shaderMatrix)
	}

	companion object {
		private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
		private val SCALE_TYPE = ScaleType.CENTER_CROP
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
		if (this is BitmapDrawable)
			return bitmap
		//如果不是BitmapDrawable，需要将drawable画到bitmap上面
		val canvas = Canvas(bitmap)
		this.setBounds(0, 0, canvas.width, canvas.height)
		this.draw(canvas)
		return bitmap
	}
}