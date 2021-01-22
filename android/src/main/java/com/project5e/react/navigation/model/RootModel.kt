package com.project5e.react.navigation.model

import com.facebook.react.bridge.ReadableMap

enum class RootType {
    TABS, STACK, SCREEN
}

interface Root {
    val type: RootType
}

data class Tabs(
    override val type: RootType,
    val pages: List<Page>,
    val options: ReadableMap? = null
) : Root

data class Screen(
    override val type: RootType,
    val page: Page
) : Root

data class Page(
    val rootName: String,
    val options: ReadableMap? = null
)
