package com.morkath.contacts.ui.contact

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.morkath.contacts.ui.theme.ContactsTheme
import com.morkath.contacts.util.VoiceSearchUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContactScreen(
    onBack: () -> Unit = {},
    onDetail: (Long) -> Unit = { },
) {
    val viewModel: ContactViewModel = hiltViewModel()
    val contacts by viewModel.contacts.collectAsState()

    // ui state
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember { mutableStateListOf<String>() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Voice search launcher
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = VoiceSearchUtil.extractResult(result.data)
            if (!spokenText.isNullOrBlank()) {
                searchQuery = spokenText
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect(searchQuery) {
        viewModel.search(searchQuery)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search...", fontSize = 16.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 16.sp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchQuery.isNotBlank()) {
                                        recentSearches.remove(searchQuery)
                                        recentSearches.add(0, searchQuery)
                                        val maxRecentSearches = 10
                                        while (recentSearches.size > maxRecentSearches) {
                                            recentSearches.removeAt(recentSearches.lastIndex)
                                        }
                                    }
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            keyboardController?.hide()
                            onBack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Quay lại"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val intent = VoiceSearchUtil.createVoiceSearchIntent("Say something...")
                            voiceLauncher.launch(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Tìm kiếm bằng giọng nói"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
                Divider(
                    color = MaterialTheme.colorScheme.secondary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (recentSearches.isNotEmpty() && searchQuery.isEmpty()) {
                item {
                    Text(
                        "Nội dung tìm kiếm gần đây",
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(recentSearches) { searchItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .clickable {
                                searchQuery = searchItem
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                // TODO: Thực hiện hành động tìm kiếm
                                println("Đang tìm kiếm từ lịch sử: $searchItem")
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Lịch sử tìm kiếm", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(searchItem)
                    }
                }
                item {
                    Button(
                        onClick = { recentSearches.clear() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )

                    ) {
                        Text("Xoá tất cả")
                    }
                }
            }

            // TODO: Hiển thị kết quả tìm kiếm
            if (searchQuery.isNotEmpty()) {
                if (contacts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            // Replace with your own vector asset or image
                            Icon(
                                imageVector = Icons.Filled.AccountCircle, // Use your own illustration here
                                contentDescription = null,
                                modifier = Modifier
                                    .size(160.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                "Không có kết quả nào",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            "Kết quả cho: \"$searchQuery\"",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun SearchScreenPreview() {
    ContactsTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        SearchContactScreen(
            onBack = {},
            onDetail = {}
        )
    }
}