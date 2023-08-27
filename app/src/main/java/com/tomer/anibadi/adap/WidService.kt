package com.tomer.anibadi.adap

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.os.IBinder
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomer.anibadi.R
import com.tomer.anibadi.databinding.SerOffLayBinding
import com.tomer.anibadi.databinding.WidgetLayBinding
import com.tomer.anibadi.modal.WidgetMod
import com.tomer.anibadi.util.Utils


class WidService : Service() {


    private val b by lazy { WidgetLayBinding.inflate(LayoutInflater.from(this)) }
    private val bc by lazy { SerOffLayBinding.inflate(LayoutInflater.from(this)) }
    private val lay = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    private val gson by lazy { Gson() }

    private lateinit var windowManager: WindowManager

    private var canMove = false
    private var isCUT = false
    private var isDown = false
    private var isLeft = false
    private lateinit var domain: RectF

    private var initial = Point(0, 0)
    private var touch = PointF(0f, 0f)

    private var reps = 0

    private lateinit var mod: WidgetMod

    private val onTvCli = View.OnClickListener {
        val v = it as TextView
        clip(v.text.toString(), v.id.toString())
    }

    override fun onBind(p0: Intent?): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val type = object : TypeToken<WidgetMod>() {}.type
        val str = intent?.getStringExtra("data")
        mod = gson.fromJson(str, type)


        val imgParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            lay,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
        imgParams.gravity = Gravity.TOP
        imgParams.gravity = Gravity.END
        imgParams.y = 140

        b.root.postDelayed(({
            val x = bc.cross.x
            val y = bc.cross.y
            domain = RectF(x, y, x + bc.cross.width + 80, y + bc.cross.height + 100)
        }), 120)

        b.apply {
            imgCate.setImageDrawable(Utils.getDr(this@WidService, mod.icon))

            tvName.text = mod.name
            tvmName.text = mod.Moname
            tvAdar.text = mod.aadNo
            tvPhno.text = mod.phno
            tvdob.text = mod.dob


            tvName.setOnClickListener(onTvCli)
            tvmName.setOnClickListener(onTvCli)
            tvPhno.setOnClickListener(onTvCli)
            tvdob.setOnClickListener(onTvCli)
            tvAdar.setOnClickListener(onTvCli)
            cardView.setOnTouchListener { _: View?, motionEvent: MotionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    isDown = true
                    b.root.postDelayed({
                        if (isDown) canMove = true
                    }, 120)
                    touch.x = motionEvent.rawX
                    touch.y = motionEvent.rawY
                    initial.x = imgParams.x
                    initial.y = imgParams.y


                } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                    if (canMove) {
                        val x = motionEvent.rawX
                        val y = motionEvent.rawY
                        if (bc.root.visibility != View.VISIBLE)
                            bc.root.visibility = View.VISIBLE
                        imgParams.x = initial.x + (touch.x - x).toInt()
                        imgParams.y = initial.y + (y - touch.y).toInt()
                        windowManager.updateViewLayout(b.root, imgParams)

                        if (domain.contains(x, y)) {
                            if (!isCUT) {
                                bc.cross.background = ContextCompat.getDrawable(this@WidService, R.drawable.round_sel)
                                isCUT = true
                            }
                        } else {
                            if (isCUT) {
                                isCUT = false
                                bc.cross.background =
                                    ColorDrawable(ContextCompat.getColor(this@WidService, R.color.trans))
                            }
                        }
                    }


                } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                    bc.root.visibility = View.INVISIBLE
                    if (!canMove) { // click condition here
                        if (b.mainCard.visibility != View.VISIBLE) b.mainCard.visibility = View.VISIBLE
                        else b.mainCard.visibility = View.GONE

                    }
                    if (isCUT) stopSelf() else {
                        val valueAnimator: ValueAnimator
                        if (motionEvent.rawX < resources.displayMetrics.widthPixels / 2) {
                            valueAnimator = ValueAnimator.ofInt(
                                imgParams.x,
                                resources.displayMetrics.widthPixels
                            )
                            isLeft = false
                        } else {
                            valueAnimator = ValueAnimator.ofInt(imgParams.x, 0)
                            isLeft = true
                        }
                        valueAnimator.duration = 142
                        valueAnimator.addUpdateListener { value: ValueAnimator ->
                            imgParams.x = value.animatedValue as Int
                            windowManager.updateViewLayout(b.root, imgParams)
                        }
                        valueAnimator.start()
                    }
                    canMove = false
                    isCUT = false
                    isDown = false
                }

                true
            }
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager


        val crosses = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            lay,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )

        clip(mod.name, "name")

        bc.root.visibility = View.INVISIBLE
        try {
            windowManager.addView(bc.root, crosses)
            windowManager.addView(b.root, imgParams)
        } catch (_: Exception) {
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(b.root)
        windowManager.removeView(bc.root)
    }

    private fun clip(lab: String, string: String) {
        val climax = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val data1 = ClipData.newPlainText(string + reps++, lab)
        climax.setPrimaryClip(data1)
    }
}