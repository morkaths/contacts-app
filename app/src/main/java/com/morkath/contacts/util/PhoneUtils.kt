package com.morkath.contacts.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

object PhoneUtils {
    private const val REQUEST_CALL_PHONE = 1001

    /**
     * Thực hiện cuộc gọi điện thoại đến số điện thoại đã chỉ định.
     *
     * @param context Context để khởi động Intent.
     * @param phoneNumber Số điện thoại người nhận cuộc gọi.
     */
    fun makePhoneCall(context: Context, phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = "tel:$phoneNumber".toUri()
        }

        // Check if CALL_PHONE permission is granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.startActivity(callIntent)
        } else {
            // If permission is not granted, request it
            if (context is Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CALL_PHONE
                )
            }
        }
    }
}
