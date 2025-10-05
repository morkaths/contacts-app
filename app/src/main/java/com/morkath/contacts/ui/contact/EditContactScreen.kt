package com.morkath.contacts.ui.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.morkath.contacts.ui.theme.ContactsTheme
import com.morkath.contacts.domain.model.Contact
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    contactId: Long?,
    viewModel: ContactViewModel = viewModel(),
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    if (contactId == null) {
        LaunchedEffect(Unit) {
            onBack()
        }
        return
    }
    val contactEdit by viewModel.getById(contactId).collectAsState(initial = null)
    var contact by remember {
        mutableStateOf(
            Contact(
                id = 0,
                name = "",
                phoneNumber = "",
                email = null,
                address = null,
                photoUri = null,
                isFavorite = false
            )
        )
    }

    val uiState = viewModel.validate(contact)
    val keyboardController = LocalSoftwareKeyboardController.current
    val isDirty = contact != contactEdit
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(contactEdit) {
        contactEdit?.let {
            contact = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa liên hệ") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isDirty) showDialog = true else onBack()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                },
                actions = {
                    // Save button
                    Button(onClick = {
                        viewModel.update(contact.copy(id = contactId))
                        onSave()
                    }) { Text("Lưu") }
                    // More options button
                    IconButton(
                        onClick = { /* TODO: Show help */ }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { /* TODO: Pick image */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Add Photo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(
                "Thêm ảnh",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = contact.name,
                onValueChange = { contact = contact.copy(name = it) },
                label = { Text("Tên") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                isError = uiState.nameError != null,
                supportingText = { uiState.nameError?.let { Text(it, color = Color.Red) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = contact.phoneNumber,
                onValueChange = { contact = contact.copy(phoneNumber = it) },
                label = { Text("Số điện thoại") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PhoneIphone,
                        contentDescription = "Phone Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                isError = uiState.phoneError != null,
                supportingText = { uiState.phoneError?.let { Text(it, color = Color.Red) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = contact.email ?: "",
                onValueChange = { contact = contact.copy(email = it) },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                isError = uiState.emailError != null,
                supportingText = { uiState.emailError?.let { Text(it, color = Color.Red) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = contact.address ?: "",
                onValueChange = { contact = contact.copy(address = it) },
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Spacer(Modifier.height(12.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Xác nhận") },
            text = { Text("Bạn có chắc muốn hủy? Mọi thay đổi sẽ không được lưu.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onBack()
                }) { Text("Loại bỏ", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Tiếp tục chỉnh sửa")
                }
            },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF23252B)
@Composable
fun PreviewEditContactScreen() {
    ContactsTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        AddContactScreen(
            onSave = {},
            onBack = {}
        )
    }
}
