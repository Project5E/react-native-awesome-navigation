//
//  ALCNativeViewController.m
//  PureNativeNavigation
//
//  Created by Skylar on 2020/8/16.
//

#import "ALCNativeViewController.h"
#import "ALCNavigationManager.h"
#import "ALCStackModel.h"

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
    UINavigationController *nav = self.navigationController;
    NSMutableArray *stack = [[ALCNavigationManager shared].stacks valueForKey:nav.screenID];
    ((ALCStackModel *)stack.lastObject).data = data;
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
