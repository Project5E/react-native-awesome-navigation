//
//  ALCStackNavigator.m
//  react-native-pure-navigation
//
//  Created by skylar on 2020/9/22.
//

#import <React/RCTConvert.h>
#import "ALCStackNavigator.h"
#import "ALCNavigationController.h"
#import "ALCNavigationManager.h"
#import "UIViewController+ALC.h"

@implementation ALCStackNavigator

- (NSArray *)supportAction {
    return @[@"push", @"pop", @"popToRoot", @"present", @"dismiss"];
}

//- (UIViewController *)createViewControllerWithLayout:(NSDictionary *)layout {
//    NSDictionary *stack = layout[@"stack"];
//    if (!stack) {
//        return nil;
//    }
//    UIViewController *vc = [[ALCNavigationManager shared] controllerWithLayout:stack[@"root"]];
//    NSString *moduleName = stack[@"root"][@"screen"][@"moduleName"];
//    NSDictionary *options = layout[@"options"];
//    NSString *title = options[@"title"];
//    UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:moduleName params:nil];
//    ALCNavigationController *nav = [[ALCNavigationController alloc] initWithRootViewController:viewController];
//    nav.title = title;
//    return nav;
//}

- (void)handleDispatch:(NSString *)screenID action:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params {
    if (![[self supportAction] containsObject:action]) {
        return;
    }
    UIWindow *window = RCTSharedApplication().delegate.window;
    UITabBarController *tbc = (UITabBarController *)window.rootViewController;
    UINavigationController *nav = tbc.selectedViewController;
    if ([action isEqualToString:@"push"]) {
        UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
        viewController.hidesBottomBarWhenPushed = YES;
        [nav pushViewController:viewController animated:true];
    } else if ([action isEqualToString:@"pop"]) {
        [nav popViewControllerAnimated:YES];
    } else if ([action isEqualToString:@"popToRoot"]) {
        [nav popToRootViewControllerAnimated:YES];
    } else if ([action isEqualToString:@"present"]) {
        UIViewController *viewController = [[ALCNavigationManager shared] fetchViewController:pageName params:params];
        NSNumber *index = params[@"isFullScreen"];
        if (index.boolValue) {
            viewController.modalPresentationStyle = UIModalPresentationFullScreen;
        }
        [nav presentViewController:viewController animated:YES completion:nil];
    } else if ([action isEqualToString:@"dismiss"]) {
        [tbc dismissViewControllerAnimated:YES completion:nil];
    }
}

@end
