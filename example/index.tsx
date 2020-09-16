import {registerComponent, setRoot, beforeRegister, setStyle} from 'react-native-pure-navigation'
import {Image} from 'react-native'
import Home from './src/Home'
import Setting from './src/Setting'
import Detail from './src/Detail'
import Present from './src/Present'
import NoNavigationBar from './src/NoNavigationBar'

setStyle({
  hideBackTitle: true,
  navigationBarColor: '#FFFFFF',
  tabBarColor: '#FFFFFF',
  tabBarItemColor: '#FF7151',
  backIcon: Image.resolveAssetSource(require('./src/image/Close.png')),
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
          icon: Image.resolveAssetSource(require('./src/image/Home.png')),
        },
        {
          component: 'Setting',
          title: '设置',
          icon: Image.resolveAssetSource(require('./src/image/Profile.png')),
        },
      ],
    },
  },
})
