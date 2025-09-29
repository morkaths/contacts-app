package com.morkath.contacts.ui.contact

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.morkath.contacts.ui.theme.AvatarColors
import com.morkath.contacts.ui.theme.ContactsTheme
import kotlinx.coroutines.launch
import com.morkath.contacts.util.PhoneUtils
import com.morkath.contacts.util.SmsUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contactId: Long?,
    context: Context,
    viewModel: ContactViewModel = viewModel(),
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
) {
    if (contactId == null) {
        LaunchedEffect(Unit) {
            onBack()
        }
        return
    }
    val contact by viewModel.getById(contactId).collectAsState(initial = null)


    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = { /* Text("Contact Detail") */ },
                navigationIcon = {
                    // Back button
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Favorite button
                    IconButton(
                        onClick = {
                            contact?.let {
                                viewModel.update(it.copy(isFavorite = !(it.isFavorite ?: false)))
                            }
                        },
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Icon(
                            imageVector =
                                if (contact?.isFavorite == true) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Contact"
                        )
                    }
                    // Edit button
                    IconButton(
                        onClick = onEdit,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Contact"
                        )
                    }
                    // Setting button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        },
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Contact Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            color = AvatarColors[contactId.hashCode() % AvatarColors.size],
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact?.name?.firstOrNull()?.uppercase() ?: "",
                        fontSize = 64.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = contact?.name ?: "Unknown",
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    ActionButton(
                        icon = Icons.Default.Call,
                        label = "Gọi",
                        onClick = {
                            PhoneUtils.makePhoneCall(context=context, phoneNumber = contact?.phoneNumber
                                ?: "")
                        }
                    )
                    ActionButton(
                        icon = Icons.Default.Message,
                        label = "Nhắn tin",
                        onClick = {
                            SmsUtils.sendSms(context=context, phoneNumber = contact?.phoneNumber ?: "")
                        }
                    )
                    ActionButton(
                        icon = Icons.Default.Videocam,
                        label = "Video"
                    )
                    ActionButton(
                        icon = Icons.Default.Email,
                        label = "Gửi email",
                        enabled = false
                    )
                }
                Spacer(Modifier.height(24.dp))
                // Contact Info
                ContactInfoItem(
                    icon = Icons.Default.Call,
                    info = contact?.phoneNumber ?: "Chưa có số điện thoại",
                    subInfo = "Điện thoại"
                )
                Spacer(Modifier.height(16.dp))
                // Connected Apps
                SectionTitle(title = "Kết nối với ứng dụng")
                ConnectedAppItem(
                    icon = Icons.Default.Call,
                    appName = "Zalo",
                    contactMethods = listOf("0902499899", "0123456789", "923913912")
                )
                ConnectedAppItem(
                    icon = Icons.Default.VideoChat,
                    appName = "Meet"
                )
                ConnectedAppItem(
                    icon = Icons.Default.Whatsapp,
                    appName = "WhatsApp"
                )
                Spacer(Modifier.height(16.dp))
                // Settings
                Box(modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)) {
                    Column {
                        SectionTitle(title = "Cài đặt liên hệ")
                        SettingItem(
                            icon = Icons.Default.MusicNote,
                            settingName = "Nhạc chuông của người liên hệ"
                        )
                        SettingItem(
                            icon = Icons.Default.GroupAdd,
                            settingName = "Thêm vào nhóm"
                        )
                        SettingItem(
                            icon = Icons.Default.Cake,
                            settingName = "Lời nhắc sinh nhật"
                        )
                        SettingItem(
                            settingName = "Chia sẻ liên hệ"
                        )
                        SettingItem(
                            icon = Icons.Default.Delete,
                            color = Color.Red,
                            settingName = "Xóa",
                            onClick = {
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                }
            }

    }

    if (showDeleteConfirmDialog) {
        DeleteConfirmationDialog(
            contactName = contact?.name ?: "",
            onConfirm = {
                viewModel.delete(contactId)
                showDeleteConfirmDialog = false
                onBack()
            },
            onDismiss = {
                showDeleteConfirmDialog = false
            }
        )
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .width(74.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            FilledIconButton(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier.fillMaxSize(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (enabled) MaterialTheme.colorScheme.primary
                    else Color(0xFF3A3B3F).copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp),
                    tint = if (enabled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                )
            }
        }
        Text(
            label,
            color = if (enabled) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
        )
    }
}

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        title,
        color = Color.Gray,
        fontSize = 16.sp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun ContactInfoItem(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Call,
    info: String,
    subInfo: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                info,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
            Text(
                subInfo,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ConnectedAppItem(
    icon: ImageVector,
    appName: String,
    contactMethods: List<String> = emptyList()
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.width(16.dp))
                Text(appName, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    contactMethods.forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable() { /* Handle method click */ }
                                .padding(16.dp)
                        ) {
                            Text(
                                method,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun SettingItem(
    icon: ImageVector = Icons.Default.Settings,
    color: Color = MaterialTheme.colorScheme.onSurface,
    settingName: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color
        )
        Spacer(Modifier.width(16.dp))
        Text(
            settingName,
            color = color,
            fontSize = 18.sp
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    contactName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Xác nhận xóa")
        },
        text = {
            Text(text = "Bạn có chắc chắn muốn xóa liên hệ \"$contactName\" không? Hành động này không thể hoàn tác.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Xóa", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Hủy")
            }
        }
    )
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ContactDetailScreenPreview() {
    ContactsTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        ContactDetailScreen(
            context = LocalContext.current,
            contactId = 1,
            onBack = {},
            onEdit = {}
        )
    }
}