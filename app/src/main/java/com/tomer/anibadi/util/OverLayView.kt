package com.tomer.anibadi.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.tomer.anibadi.R

class OverLayView : View {

    //region GLOBALS------->>>

    private val paintBlackish = Paint()
    private val paint = Paint()
    private val paintClick = Paint()
    private val rectTop = Rect(0, 0, 0, 0)
    private val rectBottom = Rect(0, 0, 0, 0)
    private val rectNO = Rect(0, 0, 0, 0)
    private val rectSide = Rect(0, 0, 0, 0)
    val mainRect = Rect(0, 0, 0, 0)


    private lateinit var bmp: Bitmap

    private var cap = false
    private var bott = 0
    private var frmae = 0

    //endregion GLOBALS------->>>

    //region CONSTRUCTORS------------->>>

    constructor(con: Context) : super(con) {
        commonCons()
    }

    constructor(con: Context, attr: AttributeSet) : super(con, attr) {
        commonCons()
    }

    private fun commonCons() {
        paintBlackish.apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            alpha = 160
        }

        paintClick.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            alpha = 0
        }

        paint.apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 8f
            isAntiAlias = true
        }

        bmp = BitmapFactory.decodeResource(resources,R.drawable.aadhar_holder)

    }


    //endregion CONSTRUCTORS------------->>>


    override fun onDraw(canvas: Canvas) {

        canvas.drawRect(rectTop, paintBlackish)
        canvas.drawRect(rectBottom, paintBlackish)
//        canvas.drawRect(mainRect, paintBlackish)

        canvas.drawRect(rectNO, paint)

        canvas.drawBitmap(bmp, null, rectSide, paintBlackish)

        if (cap) canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintClick)

    }

    fun setRect(rect: Rect) {
        this.rectNO.top = rect.top + bott - 10
        this.rectNO.bottom = rect.bottom + bott + 10
        this.rectNO.left = rect.left - 10
        this.rectNO.right = rect.right + 10
        postInvalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        val wid = (w * 0.9f).toInt()
        val hag = (wid * 0.8f).toInt()
        val bottom = (h - hag) shr 1

        bott = bottom
        rectTop.bottom = bottom
        rectBottom.top = hag + bottom
        rectBottom.bottom = h

        rectTop.right = w
        rectBottom.right = w


        mainRect.top = bottom
        mainRect.bottom = bottom + hag
        mainRect.right = w


        val wide = (w * 0.6f).toInt()
        rectSide.set((w - wide) shr 1, 180, ((w - wide) shr 1) + wide, 180 + (bmp.height * (wide / bmp.width.toFloat())).toInt())

        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun animat(boolean: Boolean) {
        postDelayed({
            if (boolean) frmae++
            else frmae--
            if (frmae == 0) return@postDelayed

            paintClick.alpha = (frmae * 28.33).toInt()
            postInvalidate()
            if (frmae != 10)
                animat(boolean)
            else {
                animat(false)
            }
        }, 10)
    }

    fun clickPhoto() {
        cap = true
        animat(true)
    }

}