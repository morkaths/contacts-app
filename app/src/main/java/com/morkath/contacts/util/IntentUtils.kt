package com.morkath.contacts.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

object IntentUtils {

    /**
     * Gửi email đến địa chỉ đã chỉ định.
     *
     * @param context Context để khởi động Intent.
     * @param email Địa chỉ email người nhận.
     */
    fun sendEmail(context: Context, email: String) {
        if (email.isBlank()) return
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Mở ứng dụng Zalo để gọi điện video đến số điện thoại đã chỉ định.
     *
     * @param context Context để khởi động Intent.
     * @param phoneNumber Số điện thoại người nhận cuộc gọi video.
     */
    fun openZalo(context: Context, phoneNumber: String) {
        if (phoneNumber.isBlank()) return

        val formattedNumber = if (phoneNumber.startsWith("+84")) {
            "0${phoneNumber.substring(3)}"
        } else {
            phoneNumber
        }

        try {
            val zaloUri = "zalovideocall://call?phone=$formattedNumber".toUri()
            val intent = Intent(Intent.ACTION_VIEW, zaloUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Zalo chưa được cài đặt hoặc không thể thực hiện cuộc gọi.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}