import {registerComponent, setRoot, beforeRegister, setStyle} from 'react-native-pure-navigation'
import {Image} from 'react-native'
import Home from './src/Home'
import Setting from './src/Setting'
import Detail from './src/Detail'
import Present from './src/Present'
import NoNavigationBar from './src/NoNavigationBar'

import CloseIcon from './src/image/Close.png'
import HomeIcon from './src/image/Home.png'
import SettingIcon from './src/image/Profile.png'

setStyle({
  hideBackTitle: true,
  hideNavigationBarShadow: true,
  navigationBarColor: '#FFFFFF',
  navigationBarItemColor: 'FF84A9',
  tabBarColor: '#FFFFFF',
  tabBarItemColor: '#FF84A9',
  backIcon: Image.resolveAssetSource(CloseIcon),
})
beforeRegister()

registerComponent('Home', Home, '/home')
registerComponent('Setting', Setting)
registerComponent('Detail', Detail)
registerComponent('Present', Present)
registerComponent('NoNavigationBar', NoNavigationBar)

setRoot({
  root: {
    tabs: {
      children: [
        {
          component: 'Home',
          title: '主页',
          icon: Image.resolveAssetSource(HomeIcon),
        },
        {
          component: 'Setting',
          title: '设置',
          icon: Image.resolveAssetSource(SettingIcon),
        },
      ],
    },
  },
})
