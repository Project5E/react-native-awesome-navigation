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
#import "UITabBar+DotBadge.h"
#import "ALCNavigationController.h"
#import "ALCReactViewController.h"
#import "ALCTabBarViewController.h"
#import "ALCStackModel.h"
#import "ALCGlobalStyle.h"
#import "ALCConstants.h"
#import "ALCNavigatorHelper.h"
#import "ALCNavigator.h"

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
    return @[NAVIGATION_EVENT];
}

RCT_EXPORT_METHOD(setRoot:(NSDictionary *)rootTree) {
    [self.manager clear];
    NSDictionary *root = rootTree[@"root"];
    UIViewController *viewController;
    if (root[@"tabs"]) {
        ALCTabBarViewController *tbc = [ALCNavigatorHelper createTabBarControllerWithLayout:root[@"tabs"]];
        for (UIViewController *vc in tbc.viewControllers) {
            [[ALCNavigationManager shared].tabStacks setObject:[NSMutableArray array] forKey:vc.screenID];
        }
        viewController = tbc;
    } else if (root[@"stack"]) {
        viewController = [ALCNavigatorHelper createNavigationControllerWithLayout:root[@"stack"]];
        [[ALCNavigationManager shared].tabStacks setObject:[NSMutableArray array] forKey:viewController.screenID];
    } else if (root[@"screen"]) {
        viewController = [ALCNavigatorHelper createScreenControllerWithLayout:root[@"screen"]];
    } else {
        NSAssert(false, @"root should be tabs、 stack or screen");
    }
    UIWindow *window = RCTSharedApplication().delegate.window;
    window.rootViewController = viewController;
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
    NSMutableArray *stack = [self.manager.tabStacks valueForKey:nav.screenID];
    ((ALCStackModel *)stack.lastObject).data = data;
}

RCT_EXPORT_METHOD(dispatch:(NSString *)screenID action:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params) {
    UIWindow *window = RCTSharedApplication().delegate.window;
    UIViewController *root = window.rootViewController;
    UINavigationController *nav;
    UITabBarController *tbc;
    if ([root isKindOfClass:[UITabBarController class]]) {
        tbc = (UITabBarController *)root;
        nav = tbc.selectedViewController;
    } else if ([root isKindOfClass:[UINavigationController class]]) {
        nav = (UINavigationController *)root;
    } else {
        nav = root;
    }
    if ([action isEqualToString:@"push"]) {
        UIViewController *viewController = [self.manager fetchViewController:pageName params:params];
        viewController.hidesBottomBarWhenPushed = YES;
        [nav pushViewController:viewController animated:true];
        viewController.hidesBottomBarWhenPushed = NO; // wtf？
    } else if ([action isEqualToString:@"pop"]) {
        [nav popViewControllerAnimated:YES];
    } else if ([action isEqualToString:@"popToRoot"]) {
        [nav popToRootViewControllerAnimated:YES];
    } else if ([action isEqualToString:@"present"]) {
        UIViewController *viewController = [self.manager fetchViewController:pageName params:params];
        NSNumber *index = params[@"isFullScreen"];
        if (index.boolValue) {
            viewController.modalPresentationStyle = UIModalPresentationFullScreen;
        }
        [nav presentViewController:viewController animated:YES completion:nil];
    } else if ([action isEqualToString:@"dismiss"]) {
        [nav dismissViewControllerAnimated:YES completion:nil];
    } else if ([action isEqualToString:@"switchTab"]) {
        NSNumber *index = params[@"index"];
        tbc.selectedIndex = index.integerValue;
    }
}

RCT_EXPORT_METHOD(registerReactComponent:(NSString *)appKey options:(NSDictionary *)options) {
    [self.manager registerReactModule:appKey options:options];
}

RCT_EXPORT_METHOD(setTabBadge:(NSArray<NSDictionary *> *)options) {
    UIWindow *window = RCTSharedApplication().delegate.window;
    ALCTabBarViewController *tbc = (ALCTabBarViewController *)window.rootViewController;
    [tbc setTabBadge:options];
}

@end
