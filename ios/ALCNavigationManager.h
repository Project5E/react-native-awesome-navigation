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

@property (nonatomic, strong) RCTBridge *bridge;

@property (nonatomic, strong, readonly) NSMutableDictionary *nativeModules;
@property (nonatomic, strong, readonly) NSMutableDictionary *reactModules;

@property (nonatomic, strong, readonly) NSMutableArray<ALCStackModel *> *stack;

+ (void)sendEvent:(NSString *)eventName data:(NSDictionary *)data;

+ (instancetype)shared;

- (void)registerNativeModule:(NSString *)moduleName forController:(Class)clazz;
- (BOOL)hasNativeModule:(NSString *)moduleName;
- (Class)nativeModuleClassFromName:(NSString *)moduleName;

- (void)registerReactModule:(NSString *)moduleName options:(NSDictionary *)options;
- (BOOL)hasReactModuleForName:(NSString *)moduleName;
- (NSDictionary *)reactModuleOptionsForKey:(NSString *)moduleName;

- (UIViewController *)fetchViewController:(NSString *)pageName params:(NSDictionary *)params;
- (UIImage *)fetchImage:(NSDictionary *)json;

- (void)push:(UIViewController *)vc;
- (void)clear;

@end
