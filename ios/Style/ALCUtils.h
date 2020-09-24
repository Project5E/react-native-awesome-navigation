//
//  ALCUtils.h
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ALCUtils : NSObject

+ (UIColor *)colorWithHexString:(NSString *)hexString;
+ (UIImage *)fetchImage:(NSDictionary *)json;

@end

NS_ASSUME_NONNULL_END
