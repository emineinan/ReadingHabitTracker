package com.hms.readinghabittracker.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.SparseArray
import com.google.android.material.textfield.TextInputEditText
import com.huawei.hms.kit.awareness.barrier.TimeBarrier
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun initTimes(): SparseArray<String> {
        val timeDescriptionMap = SparseArray<String>()
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_WEEKDAY, "Today is weekday.")
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_WEEKEND, "Today is weekend.")
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_HOLIDAY, "Today is holiday.")
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_NOT_HOLIDAY, "Today is not holiday.")
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_MORNING, "Good morning.")
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_AFTERNOON, "Good afternoon.")
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_EVENING, "Good evening.")
        timeDescriptionMap.put(TimeBarrier.TIME_CATEGORY_NIGHT, "Good night.")
        return timeDescriptionMap
    }
    
    private fun getFormattedDateString(date: Date): String {
        val dateFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat)
        return simpleDateFormat.format(date)
    }

    private fun getFormattedTimeString(date: Date): String {
        val timeFormat = "HH:mm"
        val simpleDateFormat = SimpleDateFormat(timeFormat)
        return simpleDateFormat.format(date)
    }

    fun getTimePickerListener(
        input: TextInputEditText,
        calender: Calendar
    ): TimePickerDialog.OnTimeSetListener {
        return TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)
            calender.set(Calendar.SECOND, 0)
            input.setText(
                getFormattedTimeString(calender.time)
            )
        }
    }

    fun getDatePickerListener(
        input: TextInputEditText,
        calender: Calendar
    ): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            input.setText(
                getFormattedDateString(calender.time)
            )
        }
    }
}