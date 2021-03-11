package com.project5e.react.navigation.data.react

data class RootWrapper(
    val root: Root,
)

data class Root(
    val bottomTabs: BottomTabs?,
    val component: ComponentWrapper?,
)

data class BottomTabs(
    val children: Array<ComponentWrapper>,
    val options: BottomTabsOptions?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BottomTabs

        if (!children.contentEquals(other.children)) return false
        if (options != other.options) return false

        return true
    }

    override fun hashCode(): Int {
        var result = children.contentHashCode()
        result = 31 * result + (options?.hashCode() ?: 0)
        return result
    }
}

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