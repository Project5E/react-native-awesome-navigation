//
//  ALCReactViewController.h
//  PureNativeNavigation
//
//  Created by skylar on 2020/8/17.
//

#import <UIKit/UIKit.h>
#import "UIViewController+ALC.h"

NS_ASSUME_NONNULL_BEGIN

@interface ALCReactViewController : UIViewController

- (instancetype)initWithModuleName:(NSString *)pageName options:(NSDictionary *)options;

@end

NS_ASSUME_NONNULL_END
