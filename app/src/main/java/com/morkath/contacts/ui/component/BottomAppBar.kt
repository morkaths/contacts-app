package com.morkath.contacts.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.morkath.contacts.ui.theme.ContactsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomBar(
    onFabClick: () -> Unit = {}
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Box {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            NavigationBarItem(
                selected = selectedIndex == 0,
                onClick = { selectedIndex = 0 },
                icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                label = { Text("Danh bạ") },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            NavigationBarItem(
                selected = selectedIndex == 1,
                onClick = { selectedIndex = 1 },
                icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                label = { Text("Điểm nổi bật") },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            NavigationBarItem(
                selected = selectedIndex == 2,
                onClick = { selectedIndex = 2 },
                icon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
                label = { Text("Sắp xếp") },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomBottomBarPreview() {
    ContactsTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        CustomBottomBar()
    }
}
