package com.project5e.react.navigation.data.react

data class RootWrapper(
    val root: Root,
)

data class Root(
    val bottomTabs: BottomTabs?,
    val component: ComponentWrapper?,
)

data class BottomTabs(
    val children: List<ComponentWrapper>,
    val options: BottomTabsOptions?,
)

data class ComponentWrapper(
    val component: Component
)

data class Component(
    val name: String,
    val options: ComponentOptions?
)

data class BottomTabsOptions(
    val tabBarModuleName: String?
)

data class ComponentOptions(
    val title: String?,
    val icon: ImageResolvedAssetSource?,
)

data class ImageResolvedAssetSource(
    val height: Int,
    val width: Int,
    val scale: Int,
    val uri: String,
)