//
//  ALCConstants.h
//  react-native-pure-navigation
//
//  Created by skylar on 2020/9/21.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

FOUNDATION_EXPORT NSString * const NAVIGATION_EVENT;

FOUNDATION_EXPORT NSString * const EVENT_TYPE;
FOUNDATION_EXPORT NSString * const VIEW_DID_APPEAR;
FOUNDATION_EXPORT NSString * const VIEW_DID_DISAPPEAR;
FOUNDATION_EXPORT NSString * const COMPONENT_RESULT;
FOUNDATION_EXPORT NSString * const RECLICK_TAB;

FOUNDATION_EXPORT NSString * const RESULT_TYPE;
FOUNDATION_EXPORT NSString * const RESULT_TYPE_OK;
FOUNDATION_EXPORT NSString * const RESULT_TYPE_CANCEL;
FOUNDATION_EXPORT NSString * const RESULT_DATA;

FOUNDATION_EXPORT NSString * const SCREEN_ID;

@interface ALCConstants : NSObject <RCTBridgeModule>

@end

NS_ASSUME_NONNULL_END
