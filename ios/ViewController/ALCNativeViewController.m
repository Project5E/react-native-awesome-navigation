//
//  ALCNativeViewController.m
//  PureNativeNavigation
//
//  Created by Skylar on 2020/8/16.
//

#import "ALCNativeViewController.h"
#import "ALCNavigationManager.h"
#import "ALCStackModel.h"
#import "ALCConstants.h"

@interface ALCNativeViewController ()

@property (nonatomic, assign) BOOL hideNavigationBar;

@end

@implementation ALCNativeViewController

- (instancetype)initWithModuleName:(NSString *)pageName props:(NSDictionary *)props {
    if (self = [super init]) {
        self.title = props[@"title"];
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:self.hideNavigationBar animated:animated];
}

- (void)setResult:(NSDictionary *)data {
    [ALCNavigationManager shared].resultData = data;
}

- (void)didReceiveResultData:(NSDictionary *)data {
    [ALCNavigationManager sendEvent:NAVIGATION_EVENT data:
    @{
      EVENT_TYPE: COMPONENT_RESULT,
      RESULT_DATA: data ?: [NSNull null],
      SCREEN_ID: self.screenID
    }];
}

@end
