package com.morkath.contacts.ui.contact

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.morkath.contacts.ui.component.SearchBar
import com.morkath.contacts.ui.theme.ContactsTheme
import com.morkath.contacts.ui.component.CustomBottomBar
import com.morkath.contacts.ui.component.CustomFAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    viewModel: ContactViewModel = viewModel(),
    onSearch: () -> Unit = { },
    onDetail: (Long) -> Unit = { },
    onCreate: () -> Unit = { }
) {
    val contacts by viewModel.contacts.collectAsState()
    val favouriteContacts = contacts.filter { it.isFavorite == true }

    Scaffold(
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

