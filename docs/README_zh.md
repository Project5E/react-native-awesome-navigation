<h2 align="center">
  <img src="../logo.png"/><br/>
</h2>
<h1 align="center">
  React Native Awesome Navigation
</h1>

react-native-awesome-navigation 是基于iOS `UIViewController` 和 Android `Activity/Fragment` 开发的一款原生路由库。
我们开发这个库的原因是当今流行[react-navigation](https://reactnavigation.org) 在性能方面达不到我们React Native App 的要求尤其是在Android App 体验很差。
感谢可汗学院的 [Our Transition to React Native](https://blog.khanacademy.org/our-transition-to-react-native/) 这篇文章给予我们一些灵感,
我们决定开发这个项目, 并且这个项目已经全量应用在我们的商业App 中, 我们还会对该项目持续优化更新。

使用这个库之前， 请先玩玩example，[点击这个教程](https://github.com/Project5E/react-native-awesome-navigation/wiki)可以帮助你如何玩demo

## 安装

```sh
yarn add react-native-awesome-navigation
```
或者
```
npm install react-native-awesome-navigation
```

## Android 中配置

保证配置文件符合下面的需求：

1. (React Native 0.59以下) 定义 `react-native-awesome-navigation` 在 `android/settings.gradle`:

```groovy
...
include ':react-native-awesome-navigation'
project(':react-native-awesome-navigation').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-awesome-navigation/android')
```

2. (React Native 0.59以下) 定义 Add the `react-native-awesome-navigation` as an dependency of your app in `android/app/build.gradle`:

```groovy
...
dependencies {
  ...
  implementation project(':react-native-awesome-navigation')
}
```

## iOS 中配置

使用 React Native Link (React Native 0.59 and lower)

运行 `react-native link react-native-awesome-navigation`

# Documentation

- [Android](/docs/Android_zh.md)

- [iOS](/docs/iOS_zh.md)

## 使用

在index 文件中使用如下，如果想要创建其他文件并在index 中引用也是可以。

```ts
import { Register, setStyle } from 'react-native-awesome-navigation';

import Home from './src/Home'
import TabBar from './src/TabBar'
import Setting from './src/Setting'

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

Register.beforeRegister()

// 注册组件，然后设置根页面

Register.registerComponent('Home', Home);
Register.registerComponent('Setting', Setting);
Register.registerComponent('Detail', Detail);
Register.registerComponent('Present', Present);
Register.registerComponent('NoNavigationBar', NoNavigationBar);

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
              options: {title: '主页', icon: Image.resolveAssetSource(require('./src/image/Home.png'))},
            },
          },
          {
            stack: {
              root: {
                screen: {
                  moduleName: 'Setting',
                },
              },
              options: {title: '设置', icon: Image.resolveAssetSource(require('./src/image/Profile.png'))},
            },
          },
        ],
        options: {tabBarModuleName: 'TabBar'}, // 自定义tabbar
      },
 },
})
```

支持原生页面与RN 页面混搭

目前提供两个原生页面样式设置 设置标题以及是否隐藏导航栏
```ts
Home.navigationItem = {
  title: '主页',
  hideNavigationBar: false,
}

```

## 导航

目前支持`push`, `pop`, `popToRoot`, `present`, `dismiss`, `switchTab`

`push` 传参
```ts
props.navigator.push('NativeViewController', { title: 'Native' })
```

`push` 接收返回传值
```ts
const resp = await props.navigator.push('Detail')
```

`pop` 前设值
```ts
props.navigator.setResult({qwe: 123})
props.navigator.pop()
```

`pop` 多页
```
props.navigator.popPages(2) // pop 两页(当前仅支持`push` 的页面，不支持`present` 页面)
```

`present`，与`push` 类似，第二个为传参，第三个为配置，后两个参数可不传
```ts
props.navigator.present('Present', undefined, {isFullScreen: true})
```
同`push` 一样，支持异步
```
const resp = await props.navigator.present('Present', undefined, {isFullScreen: true})

interface PresentOption {
  isFullScreen?: boolean // 仅iOS有效 是否全屏Present
  isTransparency?: boolean // 背景是否透明
  animated?: boolean // 是否有动画
  isTabBarPresented?: boolean // 标记是否从自定义TabBar Prensent
}
```

`dismiss` `present` 的反向操作 反向传参
```
props.navigator.setResult({qwe: 123})
props.navigator.dismiss()
```

`switchTab` 用于根页面自定义tabbar 切换
```
props.navigator.switchTab(0)
```

0代表第一个tab

每一个页面都会被注入各自所属的navigator 含有每页页面唯一的screenID 以及页面的module 名 通过navigator 来进行路由操作

## 全局样式
目前有以下全局样式，后续会增加更多
```ts
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
```ts
setStyle({
  hideBackTitle: true,
  hideNavigationBarShadow: true,
  navigationBarColor: '#FFFFFF',
  navigationBarItemColor: 'FF84A9',
  tabBarColor: '#FFFFFF',
  tabBarItemColor: '#FF84A9',
  backIcon: Image.resolveAssetSource(require('./src/image/Profile.png')),
})
```

使用原生tabbar 的时候，可以设置tabbar 的Badge

```ts
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
其中index 代表tabbar item 位置，dot 代表圆点，text 为badge 内的文字，hidden 为是否显示

```ts
export interface TabBadge {
  index: number
  hidden: boolean
  text?: string
  dot?: boolean
}
```

颜色只支持16进制，不支持red 等字符串

## 生命周期
每一个页面都有自己是否展示的hooks
```ts
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
```ts
Register.registerComponent('Home', Home, '/home')
Register.registerComponent('Setting', Setting)
```

使用前在首页激活
```ts
  useEffect(() => {
    router.activate('hulaqinzi://')
    return () => {
      router.inactivate()
    }
  }, [])
```

```ts
Router.open('hulaqinzi://home?key=value')
```
会解析出路径`/home`，以及参数`{key: value}`，并`push` 出`Home` 页面和传参

## hooks

### useResult

用于页面返回传参

```ts
  useResult(props.screenID, (data) => {
    console.log(data);
  })
```
type为 返回类型`ok` 或`cancel`

`ok` 为带值返回，`cancel` 为普通返回

data 是返回的数据

### useReClick

响应重复点击tabbar 事件，仅用于每一个tab 的首页(仅iOS)

```ts
  useReClick(props.screenID, () => {
    console.log('reclick');
  })
```

## Contributing

See the [contributing guide](../CONTRIBUTING.md) to learn how to contribute to the repository and the development
workflow.

[Logo designed by jemastock / Freepik](http://www.freepik.com)

## License

[MIT LISCENSE](https://github.com/Project5E/react-native-awesome-navigation/blob/master/LICENSE)

