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
    for (NSDictionary *tab in tabs) {
        NSDictionary *stack = tab[@"stack"];
        NSDictionary *screen = tab[@"screen"];
        NSDictionary *icon = stack[@"options"][@"icon"];
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
        tbc = [[ALCTabBarViewController alloc] initWithTabBarOptions:options];
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
    if (root.presentedViewController) {
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
    if ([action isEqualToString:@"push"]) {
        UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
        viewController.hidesBottomBarWhenPushed = YES;
        [(ALCNavigationController *)vc pushViewController:viewController animated:true];
    } else if ([action isEqualToString:@"pop"]) {
        [(ALCNavigationController *)vc popViewControllerAnimated:YES];
    } else if ([action isEqualToString:@"popToRoot"]) {
        [(ALCNavigationController *)vc popToRootViewControllerAnimated:NO];
    } else if ([action isEqualToString:@"present"]) {
        if (!vc) {
            vc = window.rootViewController;
        }
        UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
        NSNumber *isFullScreen = params[@"isFullScreen"];
        if (isFullScreen.boolValue) {
            viewController.modalPresentationStyle = UIModalPresentationFullScreen;
        }
        ALCNavigationController *presentNav = [[ALCNavigationController alloc] initWithRootViewController:viewController];
        presentNav.presentationController.delegate = self;
        [vc presentViewController:presentNav animated:YES completion:nil];
    } else if ([action isEqualToString:@"dismiss"]) {
        if (!vc) {
            vc = window.rootViewController;
        }
//        [[ALCNavigationManager shared] removePresentStack:vc];
        [vc dismissViewControllerAnimated:YES completion:nil];
    } else if ([action isEqualToString:@"switchTab"]) {
        ALCTabBarViewController *tbc = [self getTabBarController];
        NSNumber *index = params[@"index"];
        tbc.selectedIndex = index.integerValue;
    }
}

- (void)presentationControllerDidDismiss:(UIPresentationController *)presentationController {
    
}

@end
