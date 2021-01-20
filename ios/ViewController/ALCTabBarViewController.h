//
//  ALCTabBarViewController.h
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ALCTabBarViewController : UITabBarController

- (instancetype)initWithTabBarOptions:(NSDictionary *)options;

- (void)setTabBadge:(NSArray<NSDictionary *> *)options;

- (void)updateTabBar:(NSDictionary *)options;

@end

NS_ASSUME_NONNULL_END
