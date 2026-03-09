
package com.org.labss.ui.navigation

import android.net.Uri

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Search : Routes("search?query={query}&category={category}") {
        fun create(query: String, category: String?): String {
            val safeQuery = Uri.encode(query)
            val safeCategory = Uri.encode(category ?: "")
            return "search?query=$safeQuery&category=$safeCategory"
        }
    }
}