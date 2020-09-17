//
//  ALCNavigationBridge.m
//  router
//
//  Created by Skylar on 2020/8/15.
//

#import "ALCNavigationBridge.h"
#import <React/RCTConvert.h>
#import "ALCNavigationManager.h"
#import <React/RCTView.h>
#import "UIViewController+ALC.h"
#import "ALCNavigationController.h"
#import "ALCReactViewController.h"
#import "ALCTabBarViewController.h"
#import "ALCStackModel.h"
#import "ALCGlobalStyle.h"

@interface  ALCNavigationBridge ()

@property (nonatomic, strong) ALCNavigationManager *manager;

@end

@implementation ALCNavigationBridge

RCT_EXPORT_MODULE(ALCNavigationBridge)

- (instancetype)init {
    if (self = [super init]) {
        _manager = [ALCNavigationManager shared];
    }
    return self;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

- (NSArray<NSString *> *)supportedEvents {
    return @[@"NavigationEvent"];
}

RCT_EXPORT_METHOD(setRoot:(NSDictionary *)rootTree) {
    [self.manager clear];
    NSDictionary *root = rootTree[@"root"];
    NSArray *tabs = root[@"tabs"][@"children"];
    NSMutableArray *controllers = [NSMutableArray array];
    for (NSDictionary *tab in tabs) {
        NSString *component = tab[@"component"];
        NSString *title = tab[@"title"];
        NSDictionary *icon = tab[@"icon"];
        UIViewController *viewController = [self.manager fetchViewController:component params:nil];
        ALCNavigationController *nav = [[ALCNavigationController alloc] initWithRootViewController:viewController];
        [self.manager.stacks setObject:[NSMutableArray array] forKey:nav.screenID];
        nav.title = title;
        nav.tabBarItem.image = [self.manager fetchImage:icon];
        [controllers addObject:nav];
    }
    ALCTabBarViewController *tbc = [[ALCTabBarViewController alloc] init];
    tbc.viewControllers = controllers;
    UIWindow *window = RCTSharedApplication().delegate.window;
    window.rootViewController = tbc;
}

RCT_EXPORT_METHOD(setStyle:(NSDictionary *)styles) {
    [ALCGlobalStyle globalStyle].style = styles;
}

RCT_EXPORT_METHOD(currentRoute:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    UIWindow *window = RCTSharedApplication().delegate.window;
    UITabBarController *tbc = (UITabBarController *)window.rootViewController;
    UINavigationController *nav = tbc.selectedViewController;
    resolve(@{@"screenID" : nav.topViewController.screenID});
}

RCT_EXPORT_METHOD(setResult:(NSDictionary *)data) {
    UIWindow *window = RCTSharedApplication().delegate.window;
    UITabBarController *tbc = (UITabBarController *)window.rootViewController;
    UINavigationController *nav = tbc.selectedViewController;
    NSMutableArray *stack = [self.manager.stacks valueForKey:nav.screenID];
    ((ALCStackModel *)stack.lastObject).data = data;
}

RCT_EXPORT_METHOD(dispatch:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params) {
    UIWindow *window = RCTSharedApplication().delegate.window;
    UITabBarController *tbc = (UITabBarController *)window.rootViewController;
    UINavigationController *nav = tbc.selectedViewController;
    if ([action isEqualToString:@"push"]) {
        UIViewController *viewController = [self.manager fetchViewController:pageName params:params];
        viewController.hidesBottomBarWhenPushed = YES;
        [nav pushViewController:viewController animated:true];
    } else if ([action isEqualToString:@"pop"]) {
        [nav popViewControllerAnimated:YES];
    } else if ([action isEqualToString:@"popToRoot"]) {
        [nav popToRootViewControllerAnimated:YES];
    } else if ([action isEqualToString:@"present"]) {
        UIViewController *viewController = [self.manager fetchViewController:pageName params:params];
        [nav presentViewController:viewController animated:YES completion:nil];
    } else if ([action isEqualToString:@"dismiss"]) {
        [tbc dismissViewControllerAnimated:YES completion:nil];
    } else if ([action isEqualToString:@"switchTab"]) {
        NSNumber *index = params[@"index"];
        tbc.selectedIndex = index.integerValue;
    }
}

RCT_EXPORT_METHOD(registerReactComponent:(NSString *)appKey options:(NSDictionary *)options) {
    [self.manager registerReactModule:appKey options:options];
}

@end
