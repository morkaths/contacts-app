package com.morkath.contacts.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.morkath.contacts.ui.theme.ContactsTheme

/**
 * Một enum để định nghĩa các loại thông báo khác nhau.
 * Mỗi loại sẽ có một màu sắc và icon riêng.
 */
enum class CustomSnackbarType {
    SUCCESS,
    ERROR,
    INFO
}

/**
 * Một Composable tùy chỉnh để hiển thị Snackbar đẹp hơn.
 *
 * @param message Nội dung thông báo cần hiển thị.
 * @param type Loại thông báo (SUCCESS, ERROR, INFO) để quyết định màu sắc và icon.
 * @param onDismiss Hàm được gọi khi người dùng có thể bỏ qua snackbar (nếu có action).
 */
@Composable
fun CustomSnackbar(
    message: String,
    type: CustomSnackbarType,
    onDismiss: (() -> Unit)? = null
) {
    val containerColor: Color
    val contentColor: Color
    val icon: ImageVector

    when (type) {
        CustomSnackbarType.SUCCESS -> {
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            icon = Icons.Default.CheckCircle
        }
        CustomSnackbarType.ERROR -> {
            containerColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
            icon = Icons.Default.Error
        }
        CustomSnackbarType.INFO -> {
            containerColor = MaterialTheme.colorScheme.secondaryContainer
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            icon = Icons.Default.Info
        }
    }

    Snackbar(
        modifier = Modifier.padding(12.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        action = onDismiss?.let {
            {
                TextButton(onClick = it) {
                    Text("OK", color = contentColor)
                }
            }
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = "Snackbar Icon")
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * Preview để xem trước các loại Snackbar khác nhau.
 */
@Preview(showBackground = true, name = "Success Snackbar")
@Composable
fun SuccessSnackbarPreview() {
    ContactsTheme {
        CustomSnackbar(
            message = "Lưu liên hệ thành công!",
            type = CustomSnackbarType.SUCCESS
        )
    }
}

@Preview(showBackground = true, name = "Error Snackbar")
@Composable
fun ErrorSnackbarPreview() {
    ContactsTheme {
        CustomSnackbar(
            message = "Số điện thoại không hợp lệ.",
            type = CustomSnackbarType.ERROR,
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "Info Snackbar")
@Composable
fun InfoSnackbarPreview() {
    ContactsTheme {
        CustomSnackbar(
            message = "Đã xóa khỏi danh sách yêu thích.",
            type = CustomSnackbarType.INFO
        )
    }
}
