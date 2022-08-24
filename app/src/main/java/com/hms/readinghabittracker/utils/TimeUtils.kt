package com.hms.readinghabittracker.utils

import android.util.SparseArray
import com.huawei.hms.kit.awareness.barrier.TimeBarrier

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
}