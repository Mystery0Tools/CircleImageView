package vip.mystery0.circleimageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

class CircleImageView : ImageView {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	private lateinit var paint: Paint
	private lateinit var bitmap: Bitmap

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		println("这个时候开始测量")
	}

	override fun onDraw(canvas: Canvas) {
//		super.onDraw(canvas)
		println("这个时候开始绘制")
		canvas.drawCircle(width / 2F, height / 2F, width / 2F, paint)
	}

	/**
	 * 拦截设置图片Bitmap或Drawable
	 * setImageBitmap最终会调用setImageDrawable
	 */
	override fun setImageDrawable(drawable: Drawable?) {
		super.setImageDrawable(drawable)
		initBitmap()
	}

	/**
	 * 将Drawable转换成Bitmap，并存储起来，交给后面的onDraw使用
	 */
	private fun initBitmap() {
		//将drawable转为bitmap
		bitmap = when (drawable) {
			is BitmapDrawable -> (drawable as BitmapDrawable).bitmap
			is ColorDrawable -> Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
			else -> Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
		}
		if (drawable !is BitmapDrawable) {//如果不是BitmapDrawable，需要将drawable画到bitmap上面
			val canvas = Canvas(bitmap)
			drawable.setBounds(0, 0, canvas.width, canvas.height)
			drawable.draw(canvas)
		}
		val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
		paint = Paint()
		paint.isAntiAlias = true
		paint.shader = bitmapShader
	}
}