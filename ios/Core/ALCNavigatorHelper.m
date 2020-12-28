//
//  ALCNavigator.m
//  react-native-navigation-5e
//
//  Created by skylar on 2020/9/22.
//

#import <React/RCTConvert.h>
#import "ALCNavigatorHelper.h"
#import "ALCNavigationController.h"
#import "ALCNavigationManager.h"
#import "ALCNavigationController.h"
#import "ALCViewController.h"
#import "ALCTabBarViewController.h"
#import "UIViewController+ALC.h"
#import "ALCUtils.h"

@interface ALCNavigatorHelper () <UIAdaptivePresentationControllerDelegate>

@end

@implementation ALCNavigatorHelper

- (ALCTabBarViewController *)createTabBarControllerWithLayout:(NSDictionary *)layout {
    NSArray *tabs = layout[@"children"];
    NSDictionary *options = layout[@"options"];
    NSMutableArray *controllers = [NSMutableArray array];
    NSMutableArray *tabOptions = [NSMutableArray array];
    for (NSDictionary *tab in tabs) {
        NSDictionary *stack = tab[@"stack"];
        NSDictionary *screen = tab[@"screen"];
        NSDictionary *icon = stack[@"options"][@"icon"];
        [tabOptions addObject:stack[@"options"]];
        UIViewController *vc;
        if (stack) {
            vc = [self createNavigationControllerWithLayout:stack];
        } else if (screen) {
            vc = [self createScreenControllerWithLayout:screen];
        } else {
            NSAssert(false, @"error");
        }
        
        vc.tabBarItem.image = [ALCUtils fetchImage:icon];
        [controllers addObject:vc];
    }
    ALCTabBarViewController *tbc;
    if (options) {
        NSMutableDictionary *copied = [options mutableCopy];
        copied[@"tabs"] = [tabOptions copy];
        tbc = [[ALCTabBarViewController alloc] initWithTabBarOptions:copied];
    } else {
        tbc = [[ALCTabBarViewController alloc] init];
    }
    tbc.viewControllers = controllers;
    return tbc;
}

- (ALCNavigationController *)createNavigationControllerWithLayout:(NSDictionary *)layout {
    NSDictionary *options = layout[@"options"];
    NSString *moduleName = layout[@"root"][@"screen"][@"moduleName"];
    NSString *title = options[@"title"];
    UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:moduleName params:nil];
    ALCNavigationController *nav = [[ALCNavigationController alloc] initWithRootViewController:viewController];
    nav.title = title;
    return nav;
}

- (UIViewController *)createScreenControllerWithLayout:(NSDictionary *)layout {
    return [[ALCNavigationManager shared] fetchViewController:layout[@"moduleName"] params:nil];
}

- (ALCNavigationController *)getNavigationController {
    UIWindow *window = RCTSharedApplication().delegate.window;
    UIViewController *root = window.rootViewController;
    ALCNavigationController *nav;
    if ([root.presentedViewController isKindOfClass:[UINavigationController class]]) {
        nav = (ALCNavigationController *)root.presentedViewController;
    } else {
        switch (self.layoutType) {
            case ALCLayoutTypeTabs: {
                ALCTabBarViewController *tbc = (ALCTabBarViewController *)root;
                nav = tbc.selectedViewController;
                break;
            }
            case ALCLayoutTypeStack:
                nav = (ALCNavigationController *)root;
            default:
                break;
        }
    }
    return nav;
}

- (ALCTabBarViewController *)getTabBarController {
    UIWindow *window = RCTSharedApplication().delegate.window;
    ALCTabBarViewController *tbc;
    switch (self.layoutType) {
        case ALCLayoutTypeTabs:
            tbc = (ALCTabBarViewController *)window.rootViewController;
            break;
        default:
            break;
    }
    return tbc;
}

- (void)handleDispatch:(NSString *)screenID
                action:(NSString *)action
                  page:(NSString *)pageName
                params:(NSDictionary *)params
               resolve:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject {
    if ([action isEqualToString:@"push"]) {
        [self pushPage:pageName params:params];
    } else if ([action isEqualToString:@"pop"]) {
        [self pop];
    } else if ([action isEqualToString:@"popPages"]) {
        [self popPageWithParams:params];
    } else if ([action isEqualToString:@"popToRoot"]) {
        [self popToRoot];
    } else if ([action isEqualToString:@"present"]) {
        [self presentPage:pageName params:params];
    } else if ([action isEqualToString:@"dismiss"]) {
        [self dissmissParams:params resolve:resolve reject:reject];
    } else if ([action isEqualToString:@"switchTab"]) {
        [self switchTabWithParams:params];
    }
}

- (void)pushPage:(NSString *)pageName params:(NSDictionary *)params {
    UIViewController *vc = [self getNavigationController];
    UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
    viewController.hidesBottomBarWhenPushed = YES;
    [(ALCNavigationController *)vc pushViewController:viewController animated:true];
}

- (void)pop {
    UIViewController *vc = [self getNavigationController];
    [(ALCNavigationController *)vc popViewControllerAnimated:YES];
    [[ALCNavigationManager shared] popAndSendDataToViewController:vc.childViewControllers.lastObject];
}

- (void)popToRoot {
    UIViewController *vc = [self getNavigationController];
    [(ALCNavigationController *)vc popToRootViewControllerAnimated:YES];
}

- (void)popPageWithParams:(NSDictionary *)params {
    UIViewController *vc = [self getNavigationController];
    NSNumber *count = params[@"count"];
    if (((ALCNavigationController *)vc).childViewControllers.count > count.intValue) {
        NSUInteger index = ((ALCNavigationController *)vc).childViewControllers.count - count.intValue - 1;
        UIViewController *targetVC = (ALCNavigationController *)vc.childViewControllers[index];
        [(ALCNavigationController *)vc popToViewController:targetVC animated:YES];
    }
}

- (void)presentPage:(NSString *)pageName params:(NSDictionary *)params {
    UIViewController *vc = [self getNavigationController];
    if (!vc) {
        UIWindow *window = RCTSharedApplication().delegate.window;
        vc = window.rootViewController;
    }
    UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
    ALCNavigationController *presentNav = [[ALCNavigationController alloc] initWithRootViewController:viewController];
    NSNumber *isFullScreen = params[@"isFullScreen"];
    if (isFullScreen.boolValue) {
        presentNav.modalPresentationStyle = UIModalPresentationFullScreen;
    }
    NSNumber *isTransparency = params[@"isTransparency"];
    if (isTransparency.boolValue) {
        presentNav.modalPresentationStyle = UIModalPresentationOverFullScreen;
        presentNav.viewControllers.firstObject.view.backgroundColor = [UIColor clearColor];
    }
    NSNumber *animated = params[@"animated"];
    [vc presentViewController:presentNav animated:animated.boolValue completion:nil];
}

- (void)dissmissParams:(NSDictionary *)params resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    UIViewController *vc = [self getNavigationController];
    if (!vc) {
        UIWindow *window = RCTSharedApplication().delegate.window;
        vc = window.rootViewController;
    }
    NSNumber *animated = params[@"animated"];
    [vc dismissViewControllerAnimated:animated.boolValue completion:^{
        resolve(@1);
    }];
}

- (void)switchTabWithParams:(NSDictionary *)params  {
    ALCTabBarViewController *tbc = [self getTabBarController];
    NSNumber *index = params[@"index"];
    tbc.selectedIndex = index.integerValue;
}

@end
