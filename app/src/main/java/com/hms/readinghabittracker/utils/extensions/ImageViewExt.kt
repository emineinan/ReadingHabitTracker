package com.hms.readinghabittracker.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hms.readinghabittracker.R

fun ImageView.loadImage(source: Any) {
    Glide.with(this).load(source).error(R.drawable.ic_broken_image)
        .placeholder(R.drawable.ic_broken_image).into(this)
}
