//
//  ALCNavigator.h
//  react-native-navigation-5e
//
//  Created by skylar on 2020/9/22.
//

#import <Foundation/Foundation.h>

@class ALCNavigationController;
@class ALCTabBarViewController;

NS_ASSUME_NONNULL_BEGIN

@interface ALCNavigatorHelper : NSObject

@property (nonatomic, copy) NSString *layoutType;

- (ALCTabBarViewController *)createTabBarControllerWithLayout:(NSDictionary *)layout;
- (ALCNavigationController *)createNavigationControllerWithLayout:(NSDictionary *)layout;
- (UIViewController *)createScreenControllerWithLayout:(NSDictionary *)layout;

- (ALCNavigationController *)getNavigationController;
- (ALCTabBarViewController *)getTabBarController;

- (void)handleDispatch:(NSString *)screenID action:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params;

@end

NS_ASSUME_NONNULL_END
