package com.org.labss.analytic

import com.posthog.PostHog

object AnalyticsManager {


    fun trackScreenView(screenName: String) {
        PostHog.screen(
            screenTitle = screenName,
            properties = mapOf("category" to "navigation")
        )
    }

    fun trackSearchSubmitted(query: String, resultCount: Int) {
        PostHog.capture(
            event = "search_submitted",
            properties = mapOf(
                "query" to query,
                "result_count" to resultCount
            )
        )
    }

    fun trackProductAdded(productId: Int) {
        PostHog.capture(
            event = "product_added_to_cart",
            properties = mapOf("product_id" to productId)
        )
    }

    fun trackCategorySelected(category: String) {
        PostHog.capture(
            event = "category_selected",
            properties = mapOf("category_name" to category)
        )
    }

    fun trackSearchHistoryCleared() {
        PostHog.capture(event = "search_history_cleared")
    }

    fun isFeatureEnabled(flagKey: String): Boolean {
        return PostHog.isFeatureEnabled(flagKey)
    }

    fun trackQuantityChanged(productId: Int, action: String) {
        PostHog.capture(
            event = "product_quantity_changed",
            properties = mapOf(
                "product_id" to productId,
                "action" to action
            )
        )
    }

    fun trackFavoriteToggled(productId: Int) {
        PostHog.capture(
            event = "favorite_toggled",
            properties = mapOf("product_id" to productId)
        )
    }

    fun trackSearchHistoryItemDeleted(query: String) {
        PostHog.capture(
            event = "search_history_item_deleted",
            properties = mapOf("query" to query)
        )
    }

}