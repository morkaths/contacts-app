package com.morkath.contacts.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
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
import androidx.compose.material3.Divider
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember { mutableStateListOf<String>() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
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
                                            contentDescription = "Xóa tìm kiếm"
                                        )
                                    }
                                }
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            keyboardController?.hide()
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Quay lại"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Xử lý sự kiện voice search */ }) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Tìm kiếm bằng giọng nói"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // << QUAN TRỌNG
                        scrolledContainerColor = Color.Transparent
                    )
                )
                Divider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (recentSearches.isNotEmpty() && searchQuery.isEmpty()) {
                item {
                    Text("Nội dung tìm kiếm gần đây", style = MaterialTheme.typography.titleMedium)
                }
                items(recentSearches) { searchItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Xoá tất cả")
                    }
                }
            }

            // TODO: Hiển thị kết quả tìm kiếm thực tế khi searchQuery không rỗng
            if (searchQuery.isNotEmpty()) {
                item {
                    Text("Kết quả cho: \"$searchQuery\"", style = MaterialTheme.typography.titleMedium)
                }
                // ... hiển thị danh sách kết quả ...
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreen(navController = rememberNavController())
    }
}