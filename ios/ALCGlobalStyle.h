//
//  GlobalStyle.h
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ALCGlobalStyle : NSObject

@property (nonatomic, copy) NSDictionary *style;

@property (nonatomic, assign, readonly) BOOL hideBackTitle;

+ (instancetype)globalStyle;
- (instancetype)init NS_UNAVAILABLE;

@end

NS_ASSUME_NONNULL_END
