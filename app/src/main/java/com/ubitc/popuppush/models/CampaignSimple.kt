@file:Suppress("PropertyName")

package com.ubitc.popuppush.models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CampaignSimple {


    @SerializedName("id")
    var id: String? = null

    @SerializedName("scheduled_id")
    var scheduled_id: String? = null

    @SerializedName("name")
    var name: String? = null

    var is_repeated: Int? = null
    var start_time: String? = null
    var end_time: String? = null
    var type: String? = null
    var date: String? = null
    var days: String? = null

    private fun getDaysArray() : Array<String>{
        if (days == null) return arrayOf()
        return days!!.replace("[", "").replace("]", "")
            .replace("\"", "")
            .split(",")
            .toTypedArray()

    }

    @SuppressLint("SimpleDateFormat")
    fun getMillisStartTime(): Long {

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val mDate: Date = sdf.parse("$date $start_time") as Date
            mDate.time
            return mDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }
    fun getCalenders(): Array<Calendar> {
        val arrayOfCalendar: ArrayList<Calendar> = ArrayList()
        val arrayOfPrayers = getDaysArray()

        arrayOfPrayers.forEach { stringDay ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                val tempTime = start_time?.split(":")
                val d = when (stringDay.trim()) {
                    "Sat" -> Calendar.SATURDAY
                    "Sun" -> Calendar.SUNDAY
                    "Mon" -> Calendar.MONDAY
                    "Tue" -> Calendar.TUESDAY
                    "Wed" -> Calendar.WEDNESDAY
                    "Thu" -> Calendar.THURSDAY
                    "Fri" -> Calendar.FRIDAY
                    else -> null
                }
                set(Calendar.DAY_OF_WEEK, d!!)
                set(Calendar.HOUR_OF_DAY, tempTime!![0].toInt())
                set(Calendar.MINUTE, tempTime[1].toInt())
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 7)
                }
            }
            arrayOfCalendar.add(calendar)

        }
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY * 7,
//            pendingIntent
//        )
        return arrayOfCalendar.toTypedArray()
    }


}