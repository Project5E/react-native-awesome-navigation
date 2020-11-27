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
    if ([root.presentedViewController isKindOfClass:[ALCNavigationController class]]) {
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

- (void)handleDispatch:(NSString *)screenID action:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params {
    UIWindow *window = RCTSharedApplication().delegate.window;
    UIViewController *vc = [self getNavigationController];
    // push
    if ([action isEqualToString:@"push"]) {
        UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
        viewController.hidesBottomBarWhenPushed = YES;
        [(ALCNavigationController *)vc pushViewController:viewController animated:true];
        // pop
    } else if ([action isEqualToString:@"pop"]) {
        [(ALCNavigationController *)vc popViewControllerAnimated:YES];
        [[ALCNavigationManager shared] popAndSendDataToViewController:vc.childViewControllers.lastObject];
        // popPages
    } else if ([action isEqualToString:@"popPages"]) {
        NSNumber *count = params[@"count"];
        if (((ALCNavigationController *)vc).childViewControllers.count > count.intValue) {
            NSUInteger index = ((ALCNavigationController *)vc).childViewControllers.count - count.intValue - 1;
            UIViewController *targetVC = (ALCNavigationController *)vc.childViewControllers[index];
            [(ALCNavigationController *)vc popToViewController:targetVC animated:YES];
        }
        // popToRoot
    } else if ([action isEqualToString:@"popToRoot"]) {
        [(ALCNavigationController *)vc popToRootViewControllerAnimated:YES];
        // present
    } else if ([action isEqualToString:@"present"]) {
        if (!vc) {
            vc = window.rootViewController;
        }
        UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
        NSNumber *isFullScreen = params[@"isFullScreen"];
        ALCNavigationController *presentNav = [[ALCNavigationController alloc] initWithRootViewController:viewController];
        if (isFullScreen.boolValue) {
            presentNav.modalPresentationStyle = UIModalPresentationFullScreen;
        }
        presentNav.presentationController.delegate = self;
        [vc presentViewController:presentNav animated:YES completion:nil];
        // dismiss
    } else if ([action isEqualToString:@"dismiss"]) {
        if (!vc) {
            vc = window.rootViewController;
        }
        [vc dismissViewControllerAnimated:YES completion:nil];
        // switchTab
    } else if ([action isEqualToString:@"switchTab"]) {
        ALCTabBarViewController *tbc = [self getTabBarController];
        NSNumber *index = params[@"index"];
        tbc.selectedIndex = index.integerValue;
    }
}

- (void)presentationControllerDidDismiss:(UIPresentationController *)presentationController {
    
}

@end
