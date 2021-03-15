## Introduction

On Android side, it uses Kotlin + Jetpack to develop, that is lightweight, high performance, flexible and native. ReactNative

Android page is based on `Single Activity + Multi Fragment`, Fragment is in charge of page display, Activity is the navigation container and manage pages stack.

Jetpack Navigation as engin to make both Android and RN is able to manage navigation.

`Talk is cheap, show me the features.`

- Support more container
- Support Multi Stack(nested stack)
- Support Activity, Fragment, RN component page, and hybrid navigation
- Support both iOS and Android
- Support Activity restart(such as rotated screen)
- Unify Activity and Fragment lifecycle and connect RN component lifecycle
- Easy migration on Android(change package name directly)

# Get Started

1. Update MainApplication

add `NavigationManager.install(mReactNativeHost);` in `Application#onCreate()`.
See [example/MainApplication.java](../example/android/app/src/main/java/com/project5e/react/navigation/example/MainApplication.java)

2. Update MainActivity

MainActivity extends RnRootActivity.

```diff
-import com.facebook.react.ReactActivity;
+import com.project5e.react.navigation.view.RnRootActivity;

-public class MainActivity extends ReactActivity {
+public class MainActivity extends RnRootActivity {
-    @Override
-    protected String getMainComponentName() {
-        return "yourproject";
-    }
}
```

## Page editting

(Encourage use Fragment rather than Activity)

To create RN page，can extend either RnFragment or RnActivity. This is compatible ReactFragment/ReactActivity。

To create non-RN page, just follow native development method.

## Register page

Native: call `NavigationManager#register(key, class)` in `Application#onCreate()`

React Native: call `Register#registerComponent(key, componentClass)` in index

## Page navigation

Native:

- push / present `Navigation#navigate()`
- pop / dismiss `Navigation#navigateUp()`

Use Jetpack-Navigation

In RN page, just use`getNavController()` to get NavController instanse.

For non-RN page，Fragment can use `Navigation.findNavController(getView())` to get NavController instanse, however Activity has to build its own.

React Native：

- push `props.navigator.push()`
- present `props.navigator.present()`
- pop `props.navigator.pop()`
- dismiss `props.navigator.dismiss()`

## API

```java
## NavigationManager

getReactNativeHost() // global ReactNativeHost instanse
getReactInstanceManager() //global ReactInstanceManager instanse
setStyle() // set global style

## NavigationEmitter

sendEvent() // Encapsulate RCTDeviceEventEmitter#sendEvent()
receiveEvent() // Encapsulate RCTEventEmitter#sendEvent()
receiveTouches() // Encapsulate RCTEventEmitter#receiveTouches()


## ReadableMapExtKt
optBoolean()
optDouble
...

## Stroe // A bus implement by LiveData
reducer()
dispatch()

```

[react-native-navigation]:https://github.com/wix/react-native-navigation

[native-navigation]:https://github.com/airbnb/native-navigation

[scene]:https://github.com/bytedance/scene

[Fragmentation]:https://github.com/YoKeyword/Fragmentation
