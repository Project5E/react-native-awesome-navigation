import type {ImageResolvedAssetSource} from 'react-native'

export interface Layout {
  [index: string]: {}
}

export interface Screen extends Layout {
  screen: {
    moduleName: string
  }
}

export interface Stack extends Layout {
  stack: {
    root: Screen
    options?: {
      title: string
      icon: ImageResolvedAssetSource
    }
  }
}

export interface Tabs extends Layout {
  tabs: {
    children: Layout[]
    options?: {
      tabBarModuleName?: string
    }
  }
}
