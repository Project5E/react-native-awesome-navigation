//
//  ALCNavigator.m
//  react-native-awesome-navigation
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
#import "ALCConstants.h"
#import "ALCUtils.h"

@interface ALCNavigatorHelper () <UIAdaptivePresentationControllerDelegate>

@property (nonatomic, assign) BOOL isTabBarPresent;

@end

@implementation ALCNavigatorHelper

# pragma mark - layout

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

# pragma mark - getter

- (UIViewController *)getRootViewController {
    return RCTSharedApplication().delegate.window.rootViewController;
}

- (nullable ALCNavigationController *)getNavigationController {
    UIViewController *root = [self getRootViewController];
    ALCNavigationController *nav;
    if ([root.presentedViewController isKindOfClass:[UINavigationController class]]) {
        while (root.presentedViewController != nil) {
            root = root.presentedViewController;
        }
        nav = (ALCNavigationController *)root;
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

- (nullable ALCTabBarViewController *)getTabBarController {
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

- (nullable UIViewController *)getParentingViewController {
    ALCNavigationController *vc = [self getNavigationController];
    UIViewController *fatherVC = vc.presentingViewController;
    UIViewController *targetVC;
    if ([fatherVC isKindOfClass:[ALCTabBarViewController class]]) {
        targetVC = ((ALCTabBarViewController *)fatherVC).selectedViewController.childViewControllers.lastObject;
    } else if ([fatherVC isKindOfClass:[ALCNavigationController class]]) {
        targetVC = ((ALCNavigationController *)fatherVC).childViewControllers.lastObject;
    } else {
        targetVC = fatherVC;
    }
    return targetVC;;
}

# pragma mark - dispatch

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
    ALCNavigationController *vc = [self getNavigationController];
    UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
    viewController.hidesBottomBarWhenPushed = YES;
    [vc pushViewController:viewController animated:true];
}

- (void)pop {
    ALCNavigationController *vc = [self getNavigationController];
    [vc popViewControllerAnimated:YES];
    [[ALCNavigationManager shared] resignAndSendDataToViewController:vc.childViewControllers.lastObject];
}

- (void)popToRoot {
    ALCNavigationController *vc = [self getNavigationController];
    [vc popToRootViewControllerAnimated:YES];
}

- (void)popPageWithParams:(NSDictionary *)params {
    ALCNavigationController *vc = [self getNavigationController];
    NSNumber *count = params[@"count"];
    if (((ALCNavigationController *)vc).childViewControllers.count > count.intValue) {
        NSUInteger index = ((ALCNavigationController *)vc).childViewControllers.count - count.intValue - 1;
        UIViewController *targetVC = (ALCNavigationController *)vc.childViewControllers[index];
        [vc popToViewController:targetVC animated:YES];
    }
}

- (void)presentPage:(NSString *)pageName params:(NSDictionary *)params {
    UIViewController *vc = [self getNavigationController];
    if (!vc) {
        vc = [self getRootViewController];
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
    NSNumber *isTabBarPresented = params[@"isTabBarPresented"];
    self.isTabBarPresent = isTabBarPresented.boolValue;
}

- (void)dissmissParams:(NSDictionary *)params resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    UIViewController *vc = [self getNavigationController];
    if (!vc) {
        vc = [self getRootViewController];
    }

    UIViewController *targetVC = [self getParentingViewController];
    NSNumber *animated = params[@"animated"];
    [vc dismissViewControllerAnimated:animated.boolValue completion:^{
        resolve(@1);
        if (!self.isTabBarPresent) {
            if (targetVC) {
                [[ALCNavigationManager shared] resignAndSendDataToViewController:targetVC];
            }
        } else {
            [ALCNavigationManager sendEvent:NAVIGATION_EVENT data:
            @{
              EVENT_TYPE: COMPONENT_RESULT,
              RESULT_DATA: [ALCNavigationManager shared].resultData ?: [NSNull null],
              SCREEN_ID: [self getTabBarController].screenID
            }];
            [[ALCNavigationManager shared] clearData];
        }
    }];
}

- (void)switchTabWithParams:(NSDictionary *)params  {
    ALCTabBarViewController *tbc = [self getTabBarController];
    NSNumber *index = params[@"index"];
    tbc.selectedIndex = index.integerValue;
    [tbc updateTabBar:params];
}

@end
