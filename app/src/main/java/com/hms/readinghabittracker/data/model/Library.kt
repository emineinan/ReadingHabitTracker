package com.hms.readinghabittracker.data.model

import com.huawei.hms.site.api.model.Coordinate

data class Library(
    val name: String,
    val address: String,
    val location: Coordinate
)