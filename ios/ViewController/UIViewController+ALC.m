//
//  UIViewController+ALC.m
//  PureNativeNavigation
//
//  Created by skylar on 2020/9/4.
//

#import "UIViewController+ALC.h"

#import <objc/runtime.h>

@implementation UIViewController (ALC)

- (NSString *)screenID {
    id obj = objc_getAssociatedObject(self, _cmd);
    if (!obj) {
        obj = [[NSUUID UUID] UUIDString];
        objc_setAssociatedObject(self, @selector(screenID), obj, OBJC_ASSOCIATION_COPY_NONATOMIC);
    }
     return obj;
}

- (void)didReceiveResultData:(NSDictionary *)data type:(nonnull NSString *)type {
  
}

@end
