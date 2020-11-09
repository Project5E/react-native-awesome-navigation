//
//  ALCConstants.m
//  react-native-navigation-5e
//
//  Created by skylar on 2020/9/21.
//

#import "ALCConstants.h"

NSString * const NAVIGATION_EVENT   = @"NAVIGATION_EVENT";
NSString * const EVENT_TYPE         = @"EVENT_TYPE";
NSString * const VIEW_DID_APPEAR    = @"VIEW_DID_APPEAR";
NSString * const VIEW_DID_DISAPPEAR = @"VIEW_DID_DISAPPEAR";
NSString * const COMPONENT_RESULT   = @"COMPONENT_RESULT";
NSString * const RECLICK_TAB        = @"RECLICK_TAB";

NSString * const RESULT_TYPE        = @"RESULT_TYPE";
NSString * const RESULT_TYPE_OK     = @"RESULT_TYPE_OK";
NSString * const RESULT_TYPE_CANCEL = @"RESULT_TYPE_CANCEL";
NSString * const RESULT_DATA        = @"RESULT_DATA";
NSString * const SCREEN_ID          = @"SCREEN_ID";

@implementation ALCConstants
RCT_EXPORT_MODULE(ALCConstants);

- (NSDictionary<NSString *, NSString *> *)constantsToExport {
    return @{
             @"NAVIGATION_EVENT": NAVIGATION_EVENT,
             @"EVENT_TYPE": EVENT_TYPE,
             @"RECLICK_TAB": RECLICK_TAB,
             @"VIEW_DID_APPEAR": VIEW_DID_APPEAR,
             @"VIEW_DID_DISAPPEAR": VIEW_DID_DISAPPEAR,
             @"COMPONENT_RESULT": COMPONENT_RESULT,
             @"RESULT_DATA": RESULT_DATA,
             @"SCREEN_ID": SCREEN_ID,
            };
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

@end
