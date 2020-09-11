//
//  ALCReactViewController.m
//  PureNativeNavigation
//
//  Created by skylar on 2020/8/17.
//

#import <React/RCTRootView.h>
#import "ALCReactViewController.h"
#import "ALCNavigationManager.h"

@interface ALCReactViewController () <UIAdaptivePresentationControllerDelegate>

@property (nonatomic, assign) BOOL hideNavigationBar;

@end

@implementation ALCReactViewController

- (instancetype)initWithModuleName:(NSString *)pageName options:(NSDictionary *)options {
    if (self = [super init]) {
        NSNumber *hideNavigationBar = options[@"hideNavigationBar"];
        _hideNavigationBar = hideNavigationBar.boolValue;
        self.title = options[@"title"];
        NSMutableDictionary *copied = [options mutableCopy];
        [copied setObject:self.screenID forKey:@"screenID"];
        RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:[ALCNavigationManager shared].bridge
                                                         moduleName:pageName
                                                  initialProperties:copied];
        self.view = rootView;
    }
    return self;
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
