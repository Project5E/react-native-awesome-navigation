//
//  ALCTabBarViewController.m
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import "ALCTabBarViewController.h"
#import "ALCNavigationManager.h"
#import "UIViewController+ALC.h"

@interface ALCTabBarViewController () <UITabBarControllerDelegate>

@property (nonatomic, strong) UIViewController *previousController;

@end

@implementation ALCTabBarViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.delegate = self;
}

- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {
    if (self.previousController == viewController) {
        UINavigationController *nav = (UINavigationController *)viewController;
        [ALCNavigationManager sendEvent:@"NavigationEvent" data:
        @{
          @"event": @"did_select_tab",
          @"screen_id": nav.childViewControllers.firstObject.screenID
        }];
    }
    self.previousController = viewController;
}

@end
