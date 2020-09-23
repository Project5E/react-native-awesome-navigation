//
//  ALCScreenNavigator.m
//  react-native-pure-navigation
//
//  Created by skylar on 2020/9/22.
//

#import <React/RCTConvert.h>
#import "ALCScreenNavigator.h"
#import "ALCNavigationManager.h"
#import "UIViewController+ALC.h"

@implementation ALCScreenNavigator

- (NSArray *)supportAction {
    return @[@"present", @"dismiss"];
}

//- (UIViewController *)createViewControllerWithLayout:(NSDictionary *)layout {
//    NSDictionary *screen = layout[@"screen"];
//    if (!screen) {
//        return nil;
//    }
//    UIViewController *vc = [[ALCNavigationManager shared] fetchViewController:screen[@"moduleName"] params:nil];
////    [[ALCNavigationManager shared].stacks setObject:[NSMutableArray array] forKey:vc.screenID];
//    return vc;
//}

- (void)handleDispatch:(NSString *)screenID action:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params {
    if (![[self supportAction] containsObject:action]) {
        return;
    }
    UIWindow *window = RCTSharedApplication().delegate.window;
    UITabBarController *tbc = (UITabBarController *)window.rootViewController;
    UINavigationController *nav = tbc.selectedViewController;
    if ([action isEqualToString:@"present"]) {
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
