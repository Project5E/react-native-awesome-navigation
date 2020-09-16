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

- (void)setStyle:(NSDictionary *)style {
    _style = style;
    if ([style objectForKey:@"navigationBarColor"]) {
        UIColor *navigationBarColor = [ALCUtils colorWithHexString:[style objectForKey:@"navigationBarColor"]];
        [[UINavigationBar appearance] setBarTintColor:navigationBarColor];
    }
    if ([style objectForKey:@"navigationBarItemColor"]) {
        UIColor *navigationBarItemColor = [ALCUtils colorWithHexString:[style objectForKey:@"navigationBarItemColor"]];
        [[UINavigationBar appearance] setTintColor:navigationBarItemColor];
    }
    if ([style objectForKey:@"tabBarColor"]) {
        UIColor *tabBarColor = [ALCUtils colorWithHexString:[style objectForKey:@"tabBarColor"]];
        [[UITabBar appearance] setBarTintColor:tabBarColor];
    }
    if ([style objectForKey:@"tabBarItemColor"]) {
        UIColor *tabBarItemColor = [ALCUtils colorWithHexString:[style objectForKey:@"tabBarItemColor"]];
        [[UITabBar appearance] setTintColor:tabBarItemColor];
    }
    if ([style objectForKey:@"backIcon"]) {
        UIImage *backIcon = [ALCUtils fetchImage:[style objectForKey:@"backIcon"]];
        [[UINavigationBar appearance] setBackIndicatorImage:backIcon];
        [[UINavigationBar appearance] setBackIndicatorTransitionMaskImage:backIcon];
    }
    if ([style objectForKey:@"hideBackTitle"]) {
        NSNumber *result = (NSNumber *)[style objectForKey:@"hideBackTitle"];
        _hideBackTitle = result.boolValue;
    }
    if ([style objectForKey:@"hideBackTitle"]) {
        NSNumber *result = (NSNumber *)[style objectForKey:@"hideNavigationBarShadow"];
        if (result.boolValue) {
            [[UINavigationBar appearance] setShadowImage:[UIImage new]];
        }
    }
}


@end
