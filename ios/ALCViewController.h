//
//  ALCViewController.h
//  CocoaAsyncSocket
//
//  Created by skylar on 2020/9/16.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ALCViewController : UIViewController


@property(nonatomic, copy, readonly) NSString *pageName;
@property(nonatomic, copy, readonly) NSDictionary *props;
@property(nonatomic, copy, readonly) NSDictionary *options;

- (instancetype)initWithModuleName:(NSString *)pageName props:(NSDictionary *)props options:(NSDictionary *)options;

@end

NS_ASSUME_NONNULL_END
