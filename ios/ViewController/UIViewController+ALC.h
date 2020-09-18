//
//  UIViewController+ALC.h
//  PureNativeNavigation
//
//  Created by skylar on 2020/9/4.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIViewController (ALC)

@property (nonatomic, copy, readonly) NSString *screenID;

- (void)didReceiveResultData:(NSDictionary *)data type:(NSString *)type;

@end

NS_ASSUME_NONNULL_END
