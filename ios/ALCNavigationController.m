//
//  ALCNavigationController.m
//  PureNativeNavigation
//
//  Created by Skylar on 2020/9/5.
//

#import "ALCNavigationController.h"
#import "UIViewController+ALC.h"
#import "ALCNavigationManager.h"

@interface ALCNavigationController () <UINavigationControllerDelegate>

@end

@implementation ALCNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.delegate = self;
}

- (void)navigationController:(UINavigationController *)navigationController
      didShowViewController:(UIViewController *)viewController
                    animated:(BOOL)animated {
    [[ALCNavigationManager shared] push:viewController];
}

@end
