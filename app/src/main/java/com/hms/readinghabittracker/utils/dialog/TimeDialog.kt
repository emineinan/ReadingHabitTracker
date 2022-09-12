package com.hms.readinghabittracker.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.hms.readinghabittracker.databinding.TimeLayoutBinding

class TimeDialog {
    private var builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private val dialogBinding: TimeLayoutBinding
    private val context: Context

    companion object {
        private lateinit var instance: TimeDialog
        fun getInstance(context: Context): TimeDialog {
            instance = TimeDialog(context)
            return instance
        }
    }

    private constructor(context: Context) {
        this.context = context
        dialogBinding = TimeLayoutBinding.inflate(LayoutInflater.from(context))
        builder = AlertDialog.Builder(context).setCancelable(false)
    }

    fun setCancelButton(negativeText: String): TimeDialog {
        dialogBinding.buttonCancelTime.apply {
            visibility = View.VISIBLE
            text = negativeText
            setOnClickListener { dismissDialog() }
        }
        return this
    }

    fun setPositiveButton(
        positiveText: String,
        onClickListener: ICustomDialogClickListener,
        resultList: (ArrayList<Int>) -> Unit
    ): TimeDialog {
        dialogBinding.buttonOkTime.apply {
            visibility = View.VISIBLE
            text = positiveText
            setOnClickListener {
                val selectedTime: ArrayList<Int> = ArrayList()
                onClickListener.onClick()
                dismissDialog()
                val time =
                    checkDigit(dialogBinding.timePicker.hour) + ":" + checkDigit(dialogBinding.timePicker.minute)
                selectedTime.add(dialogBinding.timePicker.currentHour)
                selectedTime.add(dialogBinding.timePicker.currentMinute)
                resultList(selectedTime)
            }
        }
        return this
    }

    private fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    fun createDialog(): TimeDialog {
        builder.setView(dialogBinding.root)
        alertDialog = builder.create()
        dialogBinding.timePicker.setIs24HourView(true)
        return this
    }

    fun showDialog() {
        if (!this::alertDialog.isInitialized) {
            createDialog()
        }
        alertDialog.show()
    }

    fun dismissDialog() = alertDialog.dismiss()

    interface ICustomDialogClickListener {
        fun onClick()
    }
}