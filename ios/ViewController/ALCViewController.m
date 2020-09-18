//
//  ALCViewController.m
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import "ALCViewController.h"
#import "ALCNavigationManager.h"
#import "UIViewController+ALC.h"

@interface ALCViewController ()

@property (nonatomic, copy, readwrite) NSDictionary *props;

@property (nonatomic, assign) BOOL firstRenderCompleted;
@property (nonatomic, assign) BOOL viewAppeared;

@end

@implementation ALCViewController

- (instancetype)initWithModuleName:(NSString *)pageName props:(NSDictionary *)props options:(NSDictionary *)options {
    if (self = [super init]) {
        _pageName = pageName;
        _props = props;
        _options = options;
    }
    return self;
}


@end
