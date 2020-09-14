//
//  ALCNavigationController.m
//  PureNativeNavigation
//
//  Created by Skylar on 2020/9/5.
//

#import "ALCNavigationController.h"
#import "UIViewController+ALC.h"
#import "ALCNavigationManager.h"

@interface ALCNavigationController () <UINavigationControllerDelegate, UIGestureRecognizerDelegate>

@end

@implementation ALCNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.delegate = self;
    self.navigationBar.barTintColor = [UIColor whiteColor];
    self.interactivePopGestureRecognizer.delegate = self;
}

- (void)navigationController:(UINavigationController *)navigationController
       didShowViewController:(UIViewController *)viewController
                    animated:(BOOL)animated {
    [[ALCNavigationManager shared] push:navigationController vc:viewController];
}

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer {
    return self.viewControllers.count > 1;
}

@end
