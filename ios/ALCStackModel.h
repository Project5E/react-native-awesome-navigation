//
//  ALCStackModel.h
//  PureNativeNavigation
//
//  Created by Skylar on 2020/9/6.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ALCStackModel : NSObject

@property (nonatomic, copy) NSString *screenID;
@property (nonatomic, copy) NSDictionary *data;

- (instancetype)initWithScreenID:(NSString *)screenID;

@end

NS_ASSUME_NONNULL_END
