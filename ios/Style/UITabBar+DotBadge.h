//
//  UITabBar+DotBadge.h
//  react-native-navigation-5e
//
//  Created by skylar on 2020/9/18.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UITabBar (DotBadge)

- (void)showDotBadgeAtIndex:(NSInteger)index;
- (void)hideDotBadgeAtIndex:(NSInteger)index;

@end

NS_ASSUME_NONNULL_END
