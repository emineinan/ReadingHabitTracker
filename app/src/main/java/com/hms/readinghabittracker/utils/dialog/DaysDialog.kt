package com.hms.readinghabittracker.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.hms.readinghabittracker.databinding.DaysLayoutBinding

class DaysDialog {
    private var builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private val dialogBinding: DaysLayoutBinding
    private val context: Context

    companion object {
        private lateinit var instance: DaysDialog
        fun getInstance(context: Context): DaysDialog {
            instance = DaysDialog(context)
            return instance
        }
    }

    private constructor(context: Context) {
        this.context = context
        dialogBinding = DaysLayoutBinding.inflate(LayoutInflater.from(context))
        builder = AlertDialog.Builder(context).setCancelable(false)
    }

    fun setCancelButton(negativeText: String): DaysDialog {
        dialogBinding.buttonCancelDays.apply {
            visibility = View.VISIBLE
            text = negativeText
            setOnClickListener { dismissDialog() }
        }
        return this
    }

    fun setPositiveButton(
        positiveText: String,
        onClickListener: ICustomDialogClickListener,
        resultList: (ArrayList<String>) -> Unit
    ): DaysDialog {
        dialogBinding.buttonOkDays.apply {
            visibility = View.VISIBLE
            text = positiveText
            setOnClickListener {
                onClickListener.onClick()
                dismissDialog()
                val checkedDays: ArrayList<String> = ArrayList()
                if (dialogBinding.checkBoxMonday.isChecked) {
                    checkedDays.add(dialogBinding.checkBoxMonday.text.toString())
                }
                if (dialogBinding.checkBoxTuesday.isChecked) {
                    checkedDays.add(dialogBinding.checkBoxTuesday.text.toString())
                }
                if (dialogBinding.checkBoxWednesday.isChecked) {
                    checkedDays.add(dialogBinding.checkBoxWednesday.text.toString())
                }
                if (dialogBinding.checkBoxThursday.isChecked) {
                    checkedDays.add(dialogBinding.checkBoxThursday.text.toString())
                }
                if (dialogBinding.checkBoxFriday.isChecked) {
                    checkedDays.add(dialogBinding.checkBoxFriday.text.toString())
                }
                if (dialogBinding.checkBoxSaturday.isChecked) {
                    checkedDays.add(dialogBinding.checkBoxSaturday.text.toString())
                }
                if (dialogBinding.checkBoxSunday.isChecked) {
                    checkedDays.add(dialogBinding.checkBoxSunday.text.toString())
                }
                resultList(checkedDays)
            }
        }
        return this
    }

    fun createDialog(): DaysDialog {
        builder.setView(dialogBinding.root)
        alertDialog = builder.create()
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