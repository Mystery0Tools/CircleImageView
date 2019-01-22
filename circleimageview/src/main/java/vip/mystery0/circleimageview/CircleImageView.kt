package vip.mystery0.circleimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import kotlin.math.min

class CircleImageView : ImageView {

    private lateinit var config: CircleImageViewConfig
    private var bitmapWidth = 0
    private var bitmapHeight = 0
    private lateinit var bitmapPaint: Paint//绘制bitmap的画笔
    private lateinit var borderPaint: Paint//绘制边框的画笔
    private var resourceBitmap: Bitmap? = null//存储bitmap

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setConfig(config: CircleImageViewConfig): CircleImageView {
        this.config.copy(config)
        return this
    }

    fun config(listener: (CircleImageViewConfig) -> CircleImageViewConfig): CircleImageView =
        setConfig(listener.invoke(config))

    override fun onDraw(canvas: Canvas) {
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
        config = CircleImageViewConfig()
        resourceBitmap = drawable.toBitmap()
        bitmapHeight = resourceBitmap!!.height
        bitmapWidth = resourceBitmap!!.width
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