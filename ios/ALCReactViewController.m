//
//  ALCReactViewController.m
//  PureNativeNavigation
//
//  Created by skylar on 2020/8/17.
//

#import <React/RCTRootView.h>
#import "ALCReactViewController.h"
#import "ALCNavigationManager.h"
#import "ALCGlobalStyle.h"

@interface ALCReactViewController () <UIAdaptivePresentationControllerDelegate>

@property (nonatomic, assign) BOOL hideNavigationBar;

@end

@implementation ALCReactViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSNumber *hideNavigationBar = self.options[@"hideNavigationBar"];
    _hideNavigationBar = hideNavigationBar.boolValue;
    self.title = self.options[@"title"];
    NSMutableDictionary *copied = [self.options mutableCopy];
    [copied setObject:self.screenID forKey:@"screenID"];
    RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:[ALCNavigationManager shared].bridge
                                                     moduleName:self.pageName
                                              initialProperties:self.props];
    self.view = rootView;
    if ([ALCGlobalStyle globalStyle].hideBackTitle) {
        self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:NULL];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:self.hideNavigationBar animated:animated];
}

- (void)didReceiveResultData:(NSDictionary *)data type:(NSString *)type {
    [ALCNavigationManager sendEvent:@"NavigationEvent" data:
    @{
      @"event": @"component_result",
      @"result_type" : type,
      @"result_data": data ?: [NSNull null],
      @"screen_id": self.screenID
    }];
}

@end
