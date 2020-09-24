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
#import "ALCNavigatorHelper.h"

@interface ALCReactViewController () <UIAdaptivePresentationControllerDelegate>

@property (nonatomic, assign) BOOL hideNavigationBar;
@property (nonatomic, assign) BOOL firstRenderCompleted;
@property (nonatomic, assign) BOOL viewAppeared;

@end

@implementation ALCReactViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleReload)
                                                 name:RCTBridgeWillReloadNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(signalFirstRenderComplete)
                                                 name:@"FirstRenderComplete" object:nil];
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

- (void)handleReload {
    self.firstRenderCompleted = NO;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:self.hideNavigationBar animated:animated];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (!self.viewAppeared) {
        self.viewAppeared = YES;
        if (self.firstRenderCompleted) {
            [ALCNavigationManager sendEvent:NAVIGATION_EVENT data:
            @{
              EVENT_TYPE: VIEW_DID_APPEAR,
              SCREEN_ID: self.screenID
            }];
        }
    }
}


- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (!self.viewAppeared) {
        self.viewAppeared = YES;
        if (self.firstRenderCompleted) {
            [ALCNavigationManager sendEvent:NAVIGATION_EVENT data:
            @{
              EVENT_TYPE: VIEW_DID_DISAPPEAR,
              SCREEN_ID: self.screenID
            }];
        }
    }
}

- (void)signalFirstRenderComplete {
    if (self.firstRenderCompleted) {
        return;
    }
    self.firstRenderCompleted = YES;
    if (self.viewAppeared) {
        [ALCNavigationManager sendEvent:NAVIGATION_EVENT data:
        @{
          EVENT_TYPE: VIEW_DID_APPEAR,
          SCREEN_ID: self.screenID
        }];
    }
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
