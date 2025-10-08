package com.morkath.contacts.route

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.morkath.contacts.ui.contact.AddContactScreen
import com.morkath.contacts.ui.contact.ContactDetailScreen
import com.morkath.contacts.ui.contact.ContactListScreen
import com.morkath.contacts.ui.contact.ContactViewModel
import com.morkath.contacts.ui.contact.EditContactScreen
import com.morkath.contacts.ui.contact.SearchContactScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController,
        startDestination = Routes.ContactList.route
    ) {
        composable(Routes.ContactList.route) {
            ContactListScreen(
                onSearch = { navController.navigate(Routes.SearchContact.route) },
                onDetail = { id -> navController.navigate(Routes.ContactDetails.withId(id)) },
                onCreate = { navController.navigate(Routes.AddContact.route) }
            )
        }
        composable("${Routes.ContactDetails.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            ContactDetailScreen(
                contactId = id,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.EditContact.withId(id ?: 0L)) }
            )
        }
        composable(Routes.AddContact.route) {
            AddContactScreen(
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable("${Routes.EditContact.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            EditContactScreen(
                contactId = id,
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SearchContact.route) {
            SearchContactScreen(
                onBack = { navController.popBackStack() },
                onDetail = { id -> navController.navigate(Routes.ContactDetails.withId(id)) }
            )
        }
        // Add more routes as needed
    }
}