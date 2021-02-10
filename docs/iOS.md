## 初始化

使用前需要将react native 的注册
```
RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:launchOptions];
[[ALCNavigationManager shared] registerBridge:bridge];
```

路由支持原生页面
使用前需要登记该页面
```
[[ALCNavigationManager shared] registerNativeModule:@"NativeViewController" forController:[ThisIsViewController class]];
```
