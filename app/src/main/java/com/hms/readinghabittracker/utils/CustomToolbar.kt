package com.hms.readinghabittracker.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.databinding.LayoutCustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet) {
    private var binding: LayoutCustomToolbarBinding =
        LayoutCustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)
    private val obtainStyledAttributes =
        context.obtainStyledAttributes(attributeSet, R.styleable.CustomToolbar)

    init {
        if (attributeSet != null) {
            val string =
                obtainStyledAttributes.getString(R.styleable.CustomToolbar_CustomToolbar_title)

            if (string != null) {
                binding.textViewToolbarTitle.text = string
                binding.textViewToolbarTitle.textSize = 24f
            }

            val background =
                getColors(R.styleable.CustomToolbar_CustomToolbar_background, R.color.white)
            binding.appBarLayout.setBackgroundColor(background)

            val titleColor =
                getColors(
                    R.styleable.CustomToolbar_CustomToolbar_title_color,
                    R.color.primary_color_dark
                )
            binding.textViewToolbarTitle.setTextColor(titleColor)

            obtainStyledAttributes.recycle()
        }
        isClickable = true
        isFocusable = true
    }

    private fun getColors(styleable: Int, color: Int): Int {
        return obtainStyledAttributes.getColor(styleable, ContextCompat.getColor(context, color))
    }
}