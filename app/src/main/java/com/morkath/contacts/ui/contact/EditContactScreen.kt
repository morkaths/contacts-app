package com.morkath.contacts.ui.contact

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    contactId: Long?,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ContactViewModel = hiltViewModel()

    LaunchedEffect(contactId) {
        if (contactId != null) {
            viewModel.loadContactById(contactId)
        } else {
            onBack()
        }
    }
    val initialContact by viewModel.contact.collectAsState()
    var contact by remember { mutableStateOf<Contact?>(null) }
    val formUiState by viewModel.formUiState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val isDirty = contact != initialContact
    var showDialog by remember { mutableStateOf(false) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            contact = contact?.copy(photoUri = it.toString())
        }
    }

    LaunchedEffect(initialContact) {
        if (contact == null && initialContact != null) {
            contact = initialContact
        }
    }

    LaunchedEffect(contact) {
        contact?.let {
            viewModel.validate(it)
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
                        contact?.let { viewModel.update(it) }
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
        contact?.let { currentContact ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                // Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (currentContact.photoUri != null) {
                        AsyncImage(
                            model = currentContact.photoUri,
                            contentDescription = "Contact Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Text(
                    "Thêm ảnh",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = currentContact.name,
                    onValueChange = { contact = currentContact.copy(name = it) },
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
                    isError = formUiState.nameError != null,
                    supportingText = { formUiState.nameError?.let { Text(it, color = Color.Red) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = currentContact.phoneNumber,
                    onValueChange = { contact = currentContact.copy(phoneNumber = it) },
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
                    isError = formUiState.phoneError != null,
                    supportingText = {
                        formUiState.phoneError?.let {
                            Text(
                                it,
                                color = Color.Red
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = currentContact.email ?: "",
                    onValueChange = { contact = currentContact.copy(email = it) },
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
                    isError = formUiState.emailError != null,
                    supportingText = {
                        formUiState.emailError?.let {
                            Text(
                                it,
                                color = Color.Red
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = currentContact.address ?: "",
                    onValueChange = { contact = currentContact.copy(address = it) },
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
        EditContactScreen(
            contactId = 1L,
            onSave = {},
            onBack = {}
        )
    }
}
