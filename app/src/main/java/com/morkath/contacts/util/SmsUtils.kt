package com.morkath.contacts.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri


object SmsUtils {
    private const val REQUEST_SEND_SMS = 1002

    /**
     * Gửi tin nhắn SMS đến số điện thoại đã chỉ định.
     *
     * @param context Context để khởi động Intent.
     * @param phoneNumber Số điện thoại người nhận.
     * @param message Nội dung tin nhắn (mặc định là chuỗi rỗng).
     */
    fun sendSms(context: Context, phoneNumber: String, message: String? = "") {
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()
            putExtra("sms_body", message)
        }

        if (smsIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(smsIntent)
        } else {
            Toast.makeText(context, "Không tìm thấy ứng dụng SMS", Toast.LENGTH_SHORT).show()
        }
    }
}