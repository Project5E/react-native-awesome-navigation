## Why?

原生导航的好处就不在这里一一介绍了，那为什么没有选择现有的开源库，而是去造轮子呢？

简单调研过 [react-native-navigation][react-native-navigation] 和 [native-navigation][native-navigation]。下面是个人从 Android
开发的角度的一些看法，如有理解不到的地方，欢迎指正。

从代码结构设计去看。

react-native-navigation 更像是为 ReactNative 开发者而生的。也许还有点过度设计了，繁琐的设计反而导致上游扩展性差，局限于它原本的设计当中。
如果它是一个图片库或是网络库，它没问题，甚至也值得学习和借鉴。它作为一个导航库，作为提供页面控制器能力以及页面导航的库，对于用在纯 RN 项目或主 RN 的项目它也许也没太大问题。对于用在主 Android 或体量大的 App
的的话，它的迁移能力，兼容性、泛用性都显得不足，且偏离标准化开发。

native-navigation 与前者大同小异，两者对比个人更看好它，从代码结构设计来看虽说没有太多的反感，不过同样存在与前者一样的问题，而且 airbnb 已经放弃维护了。

从路由导航实现方案设计去看。

两者也都有我不太满意的地方，且同样是偏向于服务纯 RN 或主 RN 的项目，就像是仅仅为 RN 页面提供原生待遇而生的，没有提供任何用于原生的路由导航的特性。

react-native-navigation 与 native-navigation 分别采用 `Single Activity + Multi View` `Single Activity + Multi Fragment`
这两种思想作为页面骨架的设计。可以参考开源库 [scene][scene], [Fragmentation][Fragmentation]

然而~ 纵使有千万个造轮子的理由，也抵不过我们就是想造一个属于自己心目中完美的路由导航库。

所以~ 它必须可以无痛迁移，随时切换。原生开发无感知，没有多余规则。

## Introduction

这是一个用于 ReactNative 混合开发的原生路由导航库，纯 Kotlin + Jetpack 编写，轻量、高性能、自由、原生态。

基于 `Single Activity + Multi Fragment` 的思想设计，以 Fragment 为页面控制器负责页面视图的交互和展示，以 Activity 为导航容器控制导航和页面栈的管理。

基于 Jetpack Navigation 为导航引擎，实现 Android 和 RN 双端均可控制导航。

`Talk is cheap, show me the features.`

- 支持多容器
- 支持 Multi Stack（嵌套栈）
- 支持 Activity，支持 Fragment，支持 RN 组件页面，支持纯原生页面，并支持混搭导航
- 支持双端导航
- 支持 Activity 重建的恢复（旋转屏幕）
- 统一 Activity 和 Fragment 生命周期，并与 RN 组件生命周期相关联
- Android 端迁移零成本（直接替换包命）

# Get Started

在 `Application#onCreate()` 中加入 `NavigationManager.install(mReactNativeHost);`

并让 MainActivity 继承 RnRootActivity。

### 迁移到现有项目

可以在现有项目中创建命名为 ReactFragment / ReactActivity 类并继承 RnFragment / RnActivity，再全局替换`com.facebook.react.ReactFragment`
和 `com.facebook.react.ReactActivity`。

## 页面编写

(建议优先使用 Fragment，而非 Activity)

创建 RN 页面，可选择继承 RnFragment 或 RnActivity。用法兼容 RN SDK 的 ReactFragment/ReactActivity。

创建非 RN 页面，不需要做任何工作，随意就好。

## 页面注册

原生端：在 android 的 `Application#onCreate()` 中使用 `NavigationManager#register(key, class)`

RN 端：在 rn 的 index 文件中使用 `Register#registerComponent(key, componentClass)`

## 页面导航

原生端：

- push / present `Navigation#navigate()`
- pop / dismiss `Navigation#navigateUp()`

直接使用 Jetpack-Navigation，暂时没有二次包装。（还在考虑是否完全保有 Android 原生开发的原汁原味）

RN 页面中，可直接使用 `getNavController()` 获取 NavController 实例。

非 RN 页面中，Fragment 中可用 `Navigation.findNavController(getView())` 获取 NavController 实例。Activity 而需要自己创建。

RN 端：

- push `props.navigator.push()`
- present `props.navigator.present()`
- pop `props.navigator.pop()`
- dismiss `props.navigator.dismiss()`

## API

```java
## NavigationManager

getReactNativeHost() // 全局 ReactNativeHost 实例
getReactInstanceManager() //全局 ReactInstanceManager 实例
setStyle() // 调整全局样式

## NavigationEmitter

sendEvent() // 包装 RCTDeviceEventEmitter#sendEvent()
receiveEvent() // 包装RCTEventEmitter#sendEvent()
receiveTouches() // 包装 RCTEventEmitter#receiveTouches()


## ReadableMapExtKt
optBoolean()
optDouble
...

## Stroe // 一个 LiveData 实现的小 bus
reducer()
dispatch()

```

[react-native-navigation]:https://github.com/wix/react-native-navigation

[native-navigation]:https://github.com/airbnb/native-navigation

[scene]:https://github.com/bytedance/scene

[Fragmentation]:https://github.com/YoKeyword/Fragmentation
