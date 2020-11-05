import {Register, setStyle, router} from 'react-native-navigation-5e'
import {Image} from 'react-native'
import Home from './src/Home'
import Setting from './src/Setting'
import Detail from './src/Detail'
import Present from './src/Present'
import NoNavigationBar from './src/NoNavigationBar'
import TabBar from './src/TabBar'

import CloseIcon from './src/image/Close.png'
import HomeIcon from './src/image/Home.png'
import SettingIcon from './src/image/Profile.png'

// router.activate('alc://')

setStyle({
  hideBackTitle: true,
  hideNavigationBarShadow: true,
  navigationBarColor: '#FFFFFF',
  navigationBarItemColor: 'FF84A9',
  tabBarColor: '#FFFFFF',
  tabBarItemColor: '#FF84A9',
  backIcon: Image.resolveAssetSource(CloseIcon),
  tabBarDotColor: '#FF84A9',
})

Register.beforeRegister()

Register.registerComponent('Home', Home)
Register.registerComponent('Setting', Setting)
Register.registerComponent('Detail', Detail)
Register.registerComponent('Present', Present)
Register.registerComponent('NoNavigationBar', NoNavigationBar)
Register.registerComponent('TabBar', TabBar)

Register.setRoot({
  root: {
    tabs: {
      children: [
        {
          stack: {
            root: {
              screen: {
                moduleName: 'Home',
              },
            },
            options: {title: '主页', icon: Image.resolveAssetSource(HomeIcon)},
          },
        },
        {
          stack: {
            root: {
              screen: {
                moduleName: 'Setting',
              },
            },
            options: {title: '设置', icon: Image.resolveAssetSource(SettingIcon)},
          },
        },
      ],
      // options: {tabBarModuleName: 'TabBar'}, // 自定义tabbar
    },
    // stack: {
    //   root: {
    //     screen: {
    //       moduleName: 'Setting',
    //     },
    //   },
    //   options: {title: '设置', icon: Image.resolveAssetSource(SettingIcon)},
    // },
    // screen: {
    //   moduleName: 'Home',
    // },
  },
})
