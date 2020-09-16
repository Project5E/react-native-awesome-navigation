import {NativeModules} from 'react-native'

const NavigationBridge = NativeModules.ALCNavigationBridge

interface GlobalStyle {
  navigationBarColor?: string // 导航栏背景颜色
  tabBarColor?: string // tabbar背景颜色
  tabBarItemColor?: string // tabbar选中颜色
  hideBackTitle?: boolean // 是否隐藏返回按钮旁边的文字
  backIcon?: {uri: string}
}

export const setStyle = (styles: GlobalStyle) => {
  NavigationBridge.setStyle(styles)
}
