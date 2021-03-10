import type {ImageResolvedAssetSource} from 'react-native'

export interface Root {
  root: {
    bottomTabs?: BottomTabs
    component?: Component
  }
}

export interface BottomTabs {
  children: {component: Component}[]
  options?: BottomTabsOptions
}

export interface BottomTabsOptions {
  tabBarModuleName?: string
}

export interface Component {
  name: string
  options?: ComponentOptions
}

export interface ComponentOptions {
  title: string
  icon: ImageResolvedAssetSource
}
