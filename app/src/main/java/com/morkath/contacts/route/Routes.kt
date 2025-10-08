package com.morkath.contacts.route

sealed class Routes(val route: String) {
    object ContactList : Routes("contact_list")
    object SearchContact : Routes("search_contact")
    object ContactDetails : Routes("contact_details") {
        fun withId(id: Long) = "$route/$id"
    }
    object AddContact : Routes("add_contact")
    object EditContact : Routes("edit_contact") {
        fun withId(id: Long) = "$route/$id"
    }
}