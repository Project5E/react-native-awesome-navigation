//
//  GlobalStyle.h
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ALCGlobalStyle : NSObject

@property (nonatomic, strong, readonly) UIColor *navigationBarColor;
@property (nonatomic, strong, readonly) UIColor *tabBarColor;
@property (nonatomic, strong, readonly) UIColor *tabBarItemColor;
@property (nonatomic, strong, readonly) UIImage *backIcon;

@property (nonatomic, assign, readonly) BOOL hideBackTitle;

+ (instancetype)globalStyle;
- (instancetype)init NS_UNAVAILABLE;

- (void)setStyle:(NSDictionary *)style;

@end

NS_ASSUME_NONNULL_END
