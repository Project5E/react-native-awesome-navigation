//
//  ALCNavigator.m
//  react-native-pure-navigation
//
//  Created by skylar on 2020/9/22.
//

#import "ALCNavigatorHelper.h"
#import "ALCNavigationController.h"
#import "ALCNavigationManager.h"
#import "ALCNavigationController.h"
#import "ALCViewController.h"
#import "ALCTabBarViewController.h"
#import "UIViewController+ALC.h"
#import "ALCUtils.h"
@implementation ALCNavigatorHelper

+ (ALCTabBarViewController *)createTabBarControllerWithLayout:(NSDictionary *)layout {
    NSArray *tabs = layout[@"children"];
    NSDictionary *options = layout[@"options"];
    NSMutableArray *controllers = [NSMutableArray array];
    for (NSDictionary *tab in tabs) {
        NSDictionary *stack = tab[@"stack"];
        NSDictionary *screen = tab[@"screen"];
        NSDictionary *icon = stack[@"options"][@"icon"];
        UIViewController *vc;
        if (stack) {
            vc = [ALCNavigatorHelper createNavigationControllerWithLayout:stack];
        } else if (screen) {
            vc = [ALCNavigatorHelper createScreenControllerWithLayout:screen];
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

+ (ALCNavigationController *)createNavigationControllerWithLayout:(NSDictionary *)layout {
    NSDictionary *options = layout[@"options"];
    NSString *moduleName = layout[@"root"][@"screen"][@"moduleName"];
    NSString *title = options[@"title"];
    UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:moduleName params:nil];
    ALCNavigationController *nav = [[ALCNavigationController alloc] initWithRootViewController:viewController];
    nav.title = title;
    return nav;
}

+ (UIViewController *)createScreenControllerWithLayout:(NSDictionary *)layout {
    return [[ALCNavigationManager shared] fetchViewController:layout[@"moduleName"] params:nil];
}

@end
