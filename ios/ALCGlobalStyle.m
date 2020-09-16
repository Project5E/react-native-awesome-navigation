//
//  ALCGlobalStyle.m
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import "ALCGlobalStyle.h"
#import "ALCUtils.h"

@implementation ALCGlobalStyle

+ (instancetype)globalStyle {
    static ALCGlobalStyle *style;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        style = [[ALCGlobalStyle alloc] init];
    });
    return style;
}

- (instancetype)init {
    if (self = [super init]) {
    }
    return self;
}

- (void)setStyle:(NSDictionary *)style {
    if ([style objectForKey:@"navigationBarColor"]) {
        _navigationBarColor = [ALCUtils colorWithHexString:[style objectForKey:@"navigationBarColor"]];
    }
    if ([style objectForKey:@"tabBarColor"]) {
        _tabBarColor = [ALCUtils colorWithHexString:[style objectForKey:@"tabBarColor"]];
    }
    if ([style objectForKey:@"tabBarItemColor"]) {
        _tabBarItemColor = [ALCUtils colorWithHexString:[style objectForKey:@"tabBarItemColor"]];
    }
    if ([style objectForKey:@"backIcon"]) {
        _backIcon = [ALCUtils fetchImage:[style objectForKey:@"backIcon"]];
    }
    if ([style objectForKey:@"hideBackTitle"]) {
        NSNumber *result = (NSNumber *)[style objectForKey:@"hideBackTitle"];
        _hideBackTitle = result.boolValue;
    }
}

@end
