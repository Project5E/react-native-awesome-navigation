//
//  ALCStackModel.m
//  PureNativeNavigation
//
//  Created by Skylar on 2020/9/6.
//

#import "ALCStackModel.h"

@implementation ALCStackModel

- (instancetype)initWithScreenID:(NSString *)screenID {
    if (self = [super init]) {
        _screenID = screenID;
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[ALCStackModel class]]) {
        return NO;
    }
    return [self isEqualToModel:(ALCStackModel *)object];
}

- (BOOL)isEqualToModel:(ALCStackModel *)model {
    return [self.screenID isEqualToString:model.screenID];
}

- (NSUInteger)hash {
    return [self.screenID hash];
}

@end
