import {NativeModules} from 'react-native'

const NavigationBridge = NativeModules.ALCNavigationBridge

interface GlobalStyle {
  backIcon?: {uri: string} // 设置返回图标
  hideNavigationBarShadow?: boolean // 隐藏导航栏底部线
  hideBackTitle?: boolean // 是否隐藏返回按钮旁边的文字
  navigationBarColor?: string // 导航栏背景颜色
  navigationBarItemColor?: string // 导航栏item颜色

  tabBarColor?: string // tabbar背景颜色
  tabBarItemColor?: string // tabbar选中颜色
}

export const setStyle = (style: GlobalStyle) => {
  NavigationBridge.setStyle(style)
}
