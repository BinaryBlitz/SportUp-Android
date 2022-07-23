package ru.binaryblitz.SportUp.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import ru.binaryblitz.SportUp.models.AppColors
import ru.binaryblitz.SportUp.utils.AndroidUtilities

class AvatarView(context: Context, name: String) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var namePaint: TextPaint? = null
    private var color: Int = 0
    private var textLayout: StaticLayout? = null
    private var textWidth: Float = 0.toFloat()
    private var textHeight: Float = 0.toFloat()
    private var textLeft: Float = 0.toFloat()
    private var size: Int = 0

    private val DEFAULT_SIZE = AndroidUtilities.convertDpToPixel(84f, context).toInt()

    init {
        setInfo(context, name)
    }

    private fun initPaint() {
        namePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        namePaint!!.color = Color.parseColor("#ffffff")
        namePaint!!.typeface = Typeface.DEFAULT
    }

    private fun initPaintSize(context: Context) {
        setTextSize(context, 22f)
    }

    private fun setTextSize(context: Context, size: Float) {
        namePaint!!.textSize = AndroidUtilities.convertDpToPixel(size, context)
    }

    private fun setText(name: String?) {
        if (name == null || name.isEmpty()) {
            return
        }

        val text = name.substring(0, 1)
        setColor(text)
        initTextLayout(text)
    }

    private fun initTextLayout(text: String) {
        val result: String
        if (text.isNotEmpty()) {
            result = text.toUpperCase()
            try {
                val size = DEFAULT_SIZE
                textLayout = StaticLayout(result, namePaint, size, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
                if (textLayout!!.lineCount > 0) {
                    textLeft = textLayout!!.getLineLeft(0)
                    textWidth = textLayout!!.getLineWidth(0)
                    textHeight = textLayout!!.getLineBottom(0).toFloat()
                }
            } catch (e: Exception) {
            }

        } else {
            textLayout = null
        }
    }

    private fun setColor(text: String) {
        if (text.length > 1) {
            color = AppColors.AVATAR_COLORS[(text[0].toInt() + text[1].toInt()) % AppColors.AVATAR_COLORS_COUNT]
        } else {
            color = AppColors.AVATAR_COLORS[text[0].toInt() % AppColors.AVATAR_COLORS_COUNT]
        }
    }

    private fun initSize(context: Context) {
        size = getSize(context, 40f)
    }

    private fun getSize(context: Context, value: Float): Int {
        return AndroidUtilities.convertDpToPixel(value, context).toInt()
    }

    private fun setInfo(context: Context, name: String) {
        initPaint()
        initPaintSize(context)
        initSize(context)
        setText(name)
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        paint.color = color
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        val size = if (bounds.width() > this.size) bounds.width() else this.size
        canvas.drawCircle((size / 2).toFloat(), (size / 2).toFloat(), (size / 2).toFloat(), paint)

        if (textLayout != null) {
            canvas.translate((size - textWidth) / 2 - textLeft, (size - textHeight) / 2)
            textLayout!!.draw(canvas)
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}

