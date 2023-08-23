package com.tomer.anibadi.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tomer.anibadi.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

class Utils {
    companion object {
        fun date(string: String): Date {
            return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(string)!!
        }

        fun setAlarm(dob: String, con: Context, name: String) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val mon = getMonths(dob)
            when {
                mon < 6 -> calendar.add(Calendar.MONTH, (6 - mon))
                mon < 36 -> calendar.add(Calendar.MONTH, (36 - mon))
                mon < 72 -> calendar.add(Calendar.MONTH, (72 - mon))
            }
            calendar.set(Calendar.DAY_OF_MONTH, "${dob[0]}${dob[1]}".toInt())
            calendar.add(Calendar.DATE, 1)
            val intent = Intent(con.applicationContext, NotRec::class.java).apply { 
                putExtra("name",name)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                con.applicationContext,
                (name + dob).hashCode(), intent, PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = con.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
        }

        @SuppressLint("NewApi")
        fun getMonths(dob: String): Int {
            val date = date(dob)
            val cal = Calendar.getInstance()
            cal.time = date
            val p = Period.between(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)), LocalDate.now())
            return p.months + (p.years * 12)
        }

        fun getDr(con: Context, int: Int): Drawable {
            return when (int) {
                0 -> ContextCompat.getDrawable(con, R.drawable.ic_c0_6m)!!
                2 -> ContextCompat.getDrawable(con, R.drawable.ic_c3_6y)!!
                1 -> ContextCompat.getDrawable(con, R.drawable.ic_c6_3y)!!
                else -> ContextCompat.getDrawable(con, R.drawable.ic_women)!!
            }
        }

    }
}