# react-native-navigation-5e

native-navigation

## Installation

```sh
yarn add react-native-navigation-5e

# npm install react-native-navigation-5e
```

## Usage

```js
import { registerComponent, setRoot } from 'react-native-navigation-5e';

// 设置全局样式
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

// 注册组件，然后设置根页面

registerComponent('Home', Home);
registerComponent('Setting', Setting);
registerComponent('Detail', Detail);
registerComponent('Present', Present);
registerComponent('NoNavigationBar', NoNavigationBar);

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
});
```

## 导航

目前支持push,pop,popToRoot,present,dismiss,switchTab

push传参
```
props.navigator.push('NativeViewController', { title: 'Native' })
```

push接收返回传值
```
const resp = await props.navigator.push('Detail')
```

pop前设值
```
props.navigator.setResult({qwe: 123})
props.navigator.pop()
```

Present，与push类似，第二个为传参，第三个为是否全屏
```
props.navigator.present('Present', undefined, true)
```

每一个页面都会被注入各自所属的navigator  
navigator含有每页页面唯一的screenID以及页面的module名  
通过navigator来进行路由操作

## 全局样式
目前有以下全局样式，后续会增加更多
```
interface GlobalStyle {
  backIcon?: {uri: string} // 设置返回图标
  hideNavigationBarShadow?: boolean // 隐藏导航栏底部线
  hideBackTitle?: boolean // 是否隐藏返回按钮旁边的文字
  navigationBarColor?: string // 导航栏背景颜色
  navigationBarItemColor?: string // 导航栏item颜色

  tabBarColor?: string // tabbar背景颜色
  tabBarItemColor?: string // tabbar选中颜色
  tabBarDotColor?: string // tabbar圆点颜色
}
```

使用
```
setStyle({
  hideBackTitle: true,
  hideNavigationBarShadow: true,
  navigationBarColor: '#FFFFFF',
  navigationBarItemColor: 'FF84A9',
  tabBarColor: '#FFFFFF',
  tabBarItemColor: '#FF84A9',
  backIcon: Image.resolveAssetSource(CloseIcon),
})
```

使用原生tabbar的时候，可以设置tabbar的Badge

```
setTabBadge([
  {
    index: 0,
    hidden: false,
    dot: true,
  },
  {
    index: 1,
    text: '1199',
    hidden: false,
  },
])
```
其中index代表tabbar item位置，dot代表圆点，text为badge内的文字，hidden为是否显示

```
export interface TabBadge {
  index: number
  hidden: boolean
  text?: string
  dot?: boolean
}
```

颜色只支持16进制，不支持red等字符串

## 生命周期
每一个页面都有自己是否展示的hooks
```
useVisibleEffect(
    props.screenID,
    useCallback(() => {
      console.log(`${props.screenID} is visible`)
      return () => {
        console.log(`${props.screenID} is gone`)
      }
    }, [])
  )
```

## 路径导航 -- 支持DeepLink

注册的时候为页面加入路径
```
registerComponent('Home', Home, '/home')
registerComponent('Setting', Setting)
```

使用前在首页激活
```
  useEffect(() => {
    router.activate('hulaqinzi://')
    return () => {
      router.inactivate()
    }
  }, [])
```

```
Router.open('hulaqinzi://home?key=value')
```
会解析出路径/home，以及参数 {key: value}，并push出Home页面和传惨

## hooks

### useResult

用于页面返回传参

```
  useResult(props.screenID, (type, data) => {
    console.log(type);
    console.log(data);
  })
```
type为返回类型 ok 或 cancel

ok为带值返回，cancel为普通返回

data是返回的数据

### useReClick

响应重复点击tabbar事件，仅用于每一个tab的首页

```
  useReClick(props.screenID, () => {
    console.log('reclick');
  })
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
