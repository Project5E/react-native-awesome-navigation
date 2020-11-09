package io.ivan.react.navigation.model

import org.json.JSONObject

enum class RootType {
    TABS, STACK, SCREEN
}

interface Root {
    val type: RootType
}

data class Tabs(
    override val type: RootType,
    val pages: List<Page>,
    val options: JSONObject? = null,
) : Root

data class Screen(
    override val type: RootType,
    val page: Page,
) : Root

data class Page(
    val rootName: String,
    val options: JSONObject? = null,
)
