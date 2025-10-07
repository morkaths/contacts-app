package com.morkath.contacts.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageUtils {
    /**
     * Sao chép hình ảnh từ URI vào bộ nhớ trong của ứng dụng.
     *
     * @param context Context để truy cập ContentResolver và hệ thống tệp.
     * @param uri URI của hình ảnh cần sao chép.
     * @return Đường dẫn tuyệt đối đến tệp hình ảnh đã sao chép trong bộ nhớ trong, hoặc null nếu có lỗi xảy ra.
     */
    fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "${UUID.randomUUID()}.jpg"
            val file = context.getFileStreamPath(fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Xóa hình ảnh khỏi bộ nhớ trong của ứng dụng dựa trên đường dẫn tệp.
     *
     * @param imagePath Đường dẫn tuyệt đối đến tệp hình ảnh cần xóa.
     */
    fun deleteImageFromInternalStorage(imagePath: String?) {
        if (imagePath.isNullOrBlank() || imagePath.startsWith("content://")) {
            return
        }

        try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}