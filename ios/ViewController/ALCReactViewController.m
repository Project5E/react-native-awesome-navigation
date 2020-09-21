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
#import "ALCConstants.h"

@interface ALCReactViewController () <UIAdaptivePresentationControllerDelegate>

@property (nonatomic, assign) BOOL hideNavigationBar;

@end

@implementation ALCReactViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSNumber *hideNavigationBar = self.options[@"hideNavigationBar"];
    _hideNavigationBar = hideNavigationBar.boolValue;
    self.title = self.options[@"title"];
    NSMutableDictionary *copied = self.props ? [self.props mutableCopy] : [NSMutableDictionary dictionary];
    [copied setObject:self.screenID forKey:@"screenID"];
    RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:[ALCNavigationManager shared].bridge
                                                     moduleName:self.pageName
                                              initialProperties:copied];
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
    [ALCNavigationManager sendEvent:NAVIGATION_EVENT data:
    @{
      EVENT_TYPE: COMPONENT_RESULT,
      RESULT_TYPE : type,
      RESULT_DATA: data ?: [NSNull null],
      SCREEN_ID: self.screenID
    }];
}

@end
