package com.morkath.contacts.ui.contact.list

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.morkath.contacts.ui.component.SearchBar
import com.morkath.contacts.ui.theme.ContactsTheme
import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.ui.component.CustomBottomBar
import com.morkath.contacts.ui.component.CustomFAB

val sampleContacts = mutableStateListOf(
    Contact(
        id = 1L,
        name = "Nguyen Van A",
        phoneNumber = "0123456789",
        email = "a@example.com",
        address = "123 Main St",
        profilePictureUri = null,
        isFavorite = false
    ),
    Contact(
        id = 2L,
        name = "Le Thi B",
        phoneNumber = "0987654321",
        email = "b@example.com",
        address = "456 Second St",
        profilePictureUri = null,
        isFavorite = true
    ),
    Contact(
        id = 3L,
        name = "Tran Van C",
        phoneNumber = "0121987654",
        email = null,
        address = null,
        profilePictureUri = null,
        isFavorite = false
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    context: Context,
    navController: NavController,
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onClick = { navController.navigate("search") }
            )
        },
        bottomBar = {
            CustomBottomBar()
        },
        floatingActionButton = {
            CustomFAB(onClick = { navController.navigate("add_contact") })
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
                items(sampleContacts) { contact ->
                    ContactListItem(
                        contact = contact,
                        onClick = { makeCall(context, contact.phoneNumber) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomBottomBar() {
    TODO("Not yet implemented")
}

fun makeCall(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun ContactListScreenPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    ContactsTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        ContactListScreen(context = context, navController = navController)
    }
}

