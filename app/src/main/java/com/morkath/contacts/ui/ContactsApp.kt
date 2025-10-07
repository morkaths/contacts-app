package com.morkath.contacts.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.morkath.contacts.ui.contact.ContactViewModel
import com.morkath.contacts.ui.contact.AddContactScreen
import com.morkath.contacts.ui.contact.EditContactScreen
import com.morkath.contacts.ui.theme.ContactsTheme
import com.morkath.contacts.ui.contact.SearchContactScreen
import com.morkath.contacts.ui.contact.ContactListScreen
import com.morkath.contacts.ui.contact.ContactDetailScreen

@Composable
fun ContactsApp() {
    ContactsTheme(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = false
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background

        ) {
            val context = LocalContext.current
            val navController = rememberNavController()
            NavGraph(context = context, navController = navController)
        }

    }
}

@Composable
fun NavGraph(
    context: Context,
    navController: NavHostController
) {
    val contactViewModel: ContactViewModel = hiltViewModel()
    NavHost(
        navController,
        startDestination = "contacts"
    ) {
        composable("contacts") {
            ContactListScreen(
                viewModel = contactViewModel,
                onSearch = { navController.navigate("search") },
                onDetail = { id -> navController.navigate("contacts/$id") },
                onCreate = { navController.navigate("create_contact") }
            )
        }
        composable("contacts/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            ContactDetailScreen(
                contactId = id,
                context = context,
                viewModel = contactViewModel,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate("edit_contact/$id") }
            )
        }
        composable("create_contact") {
            AddContactScreen(
                context = context,
                viewModel = contactViewModel,
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable("edit_contact/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            EditContactScreen(
                context = context,
                contactId = id,
                viewModel = contactViewModel,
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable("search") {
            SearchContactScreen(
                viewModel = contactViewModel,
                onBack = { navController.popBackStack() },
                onDetail = { id -> navController.navigate("contacts/$id") }
            )
        }
        // Add more routes as needed
    }
}