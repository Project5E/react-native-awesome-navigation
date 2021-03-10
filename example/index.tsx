import {Register, setStyle} from 'react-native-awesome-navigation'
import {Image} from 'react-native'
import Home from './src/Home'
import Setting from './src/Setting'
import Detail from './src/Detail'
import Present from './src/Present'
import NoNavigationBar from './src/NoNavigationBar'
import TabBar from './src/TabBar'

// router.activate('alc://')

setStyle({
  hideBackTitle: true,
  hideNavigationBarShadow: true,
  navigationBarColor: '#FFFFFF',
  navigationBarItemColor: 'FF84A9',
  tabBarColor: '#FFFFFF',
  tabBarItemColor: '#FF84A9',
  tabBarDotColor: '#FF84A9',
  backIcon: Image.resolveAssetSource(require('./src/image/Close.png')),
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
    bottomTabs: {
      children: [
        {
          component: {
            name: 'Home',
            options: {title: '主页', icon: Image.resolveAssetSource(require('./src/image/Home.png'))},
          },
        }, {
          component: {
            name: 'Setting',
            options: {title: '设置', icon: Image.resolveAssetSource(require('./src/image/Setting.png'))},
          },
        },
      ],
      options: {tabBarModuleName: 'TabBar'},
    },
  },
})
