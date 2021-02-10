## Why?

原生路由的好处就不在这里一一介绍了，那为什么没有选择现有的开源库，而是去造轮子呢？

简单调研过 [react-native-navigation][react-native-navigation] 和 [native-navigation][native-navigation]。下面是个人从 Android 开发的角度的一些看法，如有理解不到的地方，欢迎指正。

从代码结构设计的纬度去看。

react-native-navigation 更像是为 ReactNative 开发者而生的。也许是有点过度设计了，繁琐的设计反而导致上游扩展性差，局限于它原本的设计当中。
如果它是一个图片库或是网络库，它没问题，甚至也值得学习和借鉴。它作为一个路由库，作为提供页面控制器能力以及页面导航的库来说，相对于纯RN项目或主RN的项目来说它也许也没太大问题。相对于主 Android 的混编项目来说，它的迁移性、代码兼容性、可泛用性都显得不足，且偏离标准化开发。

native-navigation 与前者大同小异，两者对比个人更看好它（不知是否和 airbnb 出品有关，个人偏爱～），从代码结构设计来看虽说没有太多的反感，不过同样存在与前者一样的问题，而且 airbnb 已经放弃维护了。

从路由实现方案设计的纬度去看。

两者我都是不太满意的，同样是偏向于服务纯RN或主RN的项目，感觉像是仅仅为RN页面提供原生待遇，没站在路由导航库的根本去思考。

react-native-navigation 是 `Single Activity + Multi View`，native-navigation 是 `Single Activity + Multi Fragment`。因为年代久远，最早提出这两种页面骨架思想的文章我已经找不到了，找到两个分别基于这两种思想实现的开源库，可作参考 [scene][scene], [Fragmentation][Fragmentation]

## Introduction

（正在编写...）

# Get Started

开始之前，先了解自己为什么需要。

## Usage

MainActivity 需要继承 RNRootActivity。并在 Application 加载时完成初始化。`NavigationManager.install(mReactNativeHost);`

关键入口都在`NavigationManager.kt`中，包括提供了全局的 `ReactNativeHost` `ReactInstanceManager` 对象，和 `GlobalStyle` 全局样式的配置。

[react-native-navigation]:https://github.com/wix/react-native-navigation
[native-navigation]:https://github.com/airbnb/native-navigation
[scene]:https://github.com/bytedance/scene
[Fragmentation]:https://github.com/YoKeyword/Fragmentation
