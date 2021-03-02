## Initialization

React Native should register before using.

```
RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:launchOptions];
[[ALCNavigationManager shared] registerBridge:bridge];
```


Navigation support native page, it is necessary to register before using.

```
[[ALCNavigationManager shared] registerNativeModule:@"NativeViewController" forController:[ThisIsViewController class]];
```