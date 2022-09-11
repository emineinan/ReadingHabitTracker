package com.hms.readinghabittracker.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            byteArrayOutputStream
        ) //Quality compression method, here 100 means no compression, store the compressed data in the BIOS
        var options = 100
        while (getImageSize(byteArrayOutputStream) > 500) {  //Cycle to determine if the compressed image is greater than 2Mb, greater than continue compression
            byteArrayOutputStream.reset() //Reset the BIOS to clear it
            //First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                byteArrayOutputStream
            ) //Here, the compression options are used to store the compressed data in the BIOS
            options -= 10 //10 less each time
        }
        return byteArrayOutputStream.toByteArray()
    }


    private fun getImageSize(byteArrayOutputStream: ByteArrayOutputStream) =
        byteArrayOutputStream.toByteArray().size / 1024
}