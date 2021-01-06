//
//  RouterManager.h
//  router
//
//  Created by Skylar on 2020/8/15.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>
#import <React/RCTEventEmitter.h>

@class ALCStackModel;

@interface ALCNavigationManager : NSObject

@property (nonatomic, strong, readonly) RCTBridge *bridge;

@property (nonatomic, strong, readonly) NSMutableDictionary *nativeModules;
@property (nonatomic, strong, readonly) NSMutableDictionary *reactModules;

@property (nonatomic, copy) NSDictionary *resultData;

+ (void)sendEvent:(NSString *)eventName data:(NSDictionary *)data;

+ (instancetype)shared;

- (void)registerBridge:(RCTBridge *)bridge;

- (void)registerNativeModule:(NSString *)moduleName forController:(Class)clazz;
- (BOOL)hasNativeModule:(NSString *)moduleName;
- (Class)nativeModuleClassFromName:(NSString *)moduleName;

- (void)registerReactModule:(NSString *)moduleName options:(NSDictionary *)options;
- (BOOL)hasReactModuleForName:(NSString *)moduleName;
- (NSDictionary *)reactModuleOptionsForKey:(NSString *)moduleName;

- (UIViewController *)fetchViewController:(NSString *)pageName params:(NSDictionary *)params;

- (void)resignAndSendDataToViewController:(UIViewController *)vc;
- (void)clearData;

@end
