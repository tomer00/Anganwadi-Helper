package com.tomer.anibadi.util

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.toRectF
import com.tomer.anibadi.R

class OverLayView : View {

    //region GLOBALS------->>>

    private val paintClear = Paint()
    private val paint = Paint()
    private var colClick = 0
    private val rectNO = Rect(0, 0, 0, 0)
    private val rectBmp = Rect(0, 0, 0, 0)
    val mainRect = Rect(0, 0, 0, 0)

    private val bmp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.aadhar_holder)

    private var cap = false
    private var bott = 0

    //endregion GLOBALS------->>>

    //region CONSTRUCTORS------------->>>

    constructor(con: Context) : super(con) {
        commonCons()
    }

    constructor(con: Context, attr: AttributeSet) : super(con, attr) {
        commonCons()
    }

    private fun commonCons() {
        paintClear.apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        paint.apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 8f
            isAntiAlias = true
        }
    }


    //endregion CONSTRUCTORS------------->>>


    override fun onDraw(canvas: Canvas) {

        canvas.drawColor(Color.argb(160, 0, 0, 0))
        canvas.drawRoundRect(mainRect.left + 40f, mainRect.top.toFloat(), mainRect.right - 40f, mainRect.bottom.toFloat(), 48f, 48f, paintClear)

        canvas.drawRoundRect(rectNO.toRectF(), rectNO.height() / 2f, rectNO.height() / 2f, paint)
        canvas.drawBitmap(bmp, null, rectBmp, null)

        if (cap) canvas.drawColor(colClick)

    }

    fun setRect(rect: Rect) {
        this.rectNO.top = rect.top + bott - 20
        this.rectNO.bottom = rect.bottom + bott + 20
        this.rectNO.left = rect.left - 32
        this.rectNO.right = rect.right + 32
        postInvalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        val wid = (w * 0.9f).toInt()
        val hag = (wid * 0.8f).toInt()
        val bottom = (h - hag) shr 1

        bott = bottom
        mainRect.top = bottom
        mainRect.bottom = bottom + hag
        mainRect.right = w


        val wide = (w * 0.6f).toInt()
        rectBmp.set((w - wide) shr 1, 180, ((w - wide) shr 1) + wide, 180 + (bmp.height * (wide / bmp.width.toFloat())).toInt())

        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun animateCli() {

        ValueAnimator.ofInt(0, 255, 0).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 180
            start()
        }.addUpdateListener {
            colClick = Color.argb(it.animatedValue as Int, 255, 255, 255)
            postInvalidate()
        }
    }

    fun clickPhoto() {
        cap = true
        animateCli()
    }

}