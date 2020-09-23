//
//  ALCNavigator.h
//  react-native-pure-navigation
//
//  Created by skylar on 2020/9/22.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol ALCNavigator <NSObject>

- (UIViewController *)createViewControllerWithLayout:(NSDictionary *)layout;
- (void)handleDispatch:(NSString *)screenID action:(NSString *)action page:(NSString *)pageName params:(NSDictionary *)params;

@end

NS_ASSUME_NONNULL_END
