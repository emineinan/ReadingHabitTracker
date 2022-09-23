package com.hms.readinghabittracker.data.model

import android.os.Parcelable
import com.huawei.hms.site.api.model.Coordinate
import kotlinx.parcelize.Parcelize

@Parcelize
data class Library(
    val name: String,
    val address: String,
    val location: Coordinate
):Parcelable