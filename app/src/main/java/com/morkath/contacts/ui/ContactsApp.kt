package com.morkath.contacts.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.internal.NavContext
import com.morkath.contacts.domain.model.Contact
import com.morkath.contacts.ui.contact.detail.ContactDetailScreen
import com.morkath.contacts.ui.theme.ContactsTheme
import com.morkath.contacts.ui.contact.list.ContactListScreen
import com.morkath.contacts.ui.search.SearchScreen

@Composable
fun ContactsApp() {
    ContactsTheme(
        darkTheme = false,
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
fun NavGraph(context: Context, navController: NavHostController) {
    NavHost(navController, startDestination = "contacts") {
        composable("contacts") { ContactListScreen(context = context,navController = navController) }
        composable("search") { SearchScreen(navController = navController) }
        // Add more routes as needed
    }
}