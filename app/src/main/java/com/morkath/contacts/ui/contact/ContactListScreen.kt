package com.morkath.contacts.ui.contact

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.morkath.contacts.ui.theme.ContactsTheme
import com.morkath.contacts.ui.component.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    onSearch: () -> Unit = { },
    onDetail: (Long) -> Unit = { },
    onCreate: () -> Unit = { }
) {
    val context = LocalContext.current
    val viewModel: ContactViewModel = hiltViewModel()
    val contacts by viewModel.contacts.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val favouriteContacts = contacts.filter { it.isFavorite == true }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val readGranted = permissions[Manifest.permission.READ_CONTACTS] ?: false
            val writeGranted = permissions[Manifest.permission.WRITE_CONTACTS] ?: false

            if (readGranted) {
                // Nếu có quyền đọc, tiến hành nhập danh bạ
                viewModel.importDeviceContacts()
            } else {
                // Nếu quyền đọc bị từ chối, hiển thị thông báo
                scope.launch {
                    snackbarHostState.showSnackbar("Bạn đã từ chối quyền đọc danh bạ.")
                }
            }

            if (!writeGranted) {
                // Có thể hiển thị thông báo nếu quyền ghi bị từ chối (tùy chọn)
                scope.launch {
                    snackbarHostState.showSnackbar("Lưu ý: Bạn sẽ không thể đồng bộ liên hệ mới vào thiết bị.")
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        // 1. Kiểm tra xem đã có các quyền cần thiết chưa
        val hasReadPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        // 2. Tạo danh sách các quyền còn thiếu
        val permissionsToRequest = mutableListOf<String>()
        if (!hasReadPermission) {
            permissionsToRequest.add(Manifest.permission.READ_CONTACTS)
        }
        if (!hasWritePermission) {
            permissionsToRequest.add(Manifest.permission.WRITE_CONTACTS)
        }

        // 3. Nếu có quyền còn thiếu, hiện hộp thoại xin quyền
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(
                    message = data.visuals.message,
                    type = CustomSnackbarType.INFO
                )
            }
        },
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues()),
                onClick = { onSearch() }
            )
        },
        bottomBar = {
            CustomBottomBar()
        },
        floatingActionButton = {
            CustomFAB(onClick = { onCreate() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Contact List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if(favouriteContacts.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(15.dp),
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Favorite",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Yêu thích",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    items(favouriteContacts) { contact ->
                        ContactListItem(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                            contact = contact,
                            onClick = { onDetail(contact.id) }
                        )
                    }
                }
                item {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(start = 20.dp, top = 8.dp, bottom = 4.dp)
                    )
                }
                items(contacts) { contact ->
                    ContactListItem(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                        contact = contact,
                        onClick = { onDetail(contact.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactListScreenPreview() {
    ContactsTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        ContactListScreen(
            onSearch = { },
            onDetail = { },
            onCreate = { }
        )
    }
}

