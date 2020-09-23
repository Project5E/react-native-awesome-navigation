//
//  ALCTabBarNavigator.m
//  react-native-pure-navigation
//
//  Created by skylar on 2020/9/22.
//

#import <React/RCTConvert.h>
#import "ALCTabBarNavigator.h"
#import "ALCNavigationManager.h"
#import "ALCTabBarViewController.h"
#import "ALCUtils.h"
#import "UIViewController+ALC.h"

@implementation ALCTabBarNavigator

//- (UIViewController *)createViewControllerWithLayout:(NSDictionary *)layout {
//    NSDictionary *tabs = layout[@"tabs"];
//    if (!tabs) {
//        return nil;
//    }
//    NSArray *children = tabs[@"children"];
//    NSDictionary *options = tabs[@"options"];
//    NSMutableArray *controllers = [NSMutableArray array];
//    for (NSDictionary *tab in children) {
//        UIViewController *vc = [[ALCNavigationManager shared] controllerWithLayout:tab];
//        [[ALCNavigationManager shared].stacks setObject:[NSMutableArray array] forKey:vc.screenID];
//        [controllers addObject:vc];
//    }
//    ALCTabBarViewController *tbc;
//    if (options) {
//        tbc = [[ALCTabBarViewController alloc] initWithTabBarOptions:options];
//    } else {
//        tbc = [[ALCTabBarViewController alloc] init];
//    }
//    tbc.viewControllers = controllers;
//    return tbc;
//}

- (void)handleDispatch:(NSString *)screenID action:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params {
    
    UIWindow *window = RCTSharedApplication().delegate.window;
    UITabBarController *tbc = (UITabBarController *)window.rootViewController;
    if ([action isEqualToString:@"switchTab"]) {
        NSNumber *index = params[@"index"];
        tbc.selectedIndex = index.integerValue;
    }
}

@end
