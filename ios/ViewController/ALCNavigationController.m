//
//  ALCNavigationController.m
//  PureNativeNavigation
//
//  Created by Skylar on 2020/9/5.
//

#import "ALCNavigationController.h"
#import "UIViewController+ALC.h"
#import "ALCNavigationManager.h"

@interface ALCNavigationController () <UIGestureRecognizerDelegate>

@end

@implementation ALCNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.interactivePopGestureRecognizer.delegate = self;
}

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer {
    return self.viewControllers.count > 1;
}

@end
