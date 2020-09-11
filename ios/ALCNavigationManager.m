//
//  ALCNavigationManager.m
//  router
//
//  Created by Skylar on 2020/8/15.
//

#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#import <React/RCTRootView.h>
#import "ALCNavigationManager.h"
#import "ALCNativeViewController.h"
#import "ALCReactViewController.h"
#import "ALCStackModel.h"

@interface ALCNavigationManager()

@property (nonatomic, strong, readwrite) NSMutableDictionary *nativeModules;
@property (nonatomic, strong, readwrite) NSMutableDictionary *reactModules;

@end

@implementation ALCNavigationManager

+ (void)sendEvent:(NSString *)eventName data:(NSDictionary *)data {
    ALCNavigationManager *manager = [ALCNavigationManager shared];
    RCTBridge *bride = manager.bridge;
    if (bride.valid) {
        RCTEventEmitter *emitter = [bride moduleForName:@"ALCNavigationBridge"];
        [emitter sendEventWithName:eventName body:data];
    }
}

+ (instancetype)shared {
    static ALCNavigationManager *manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[ALCNavigationManager alloc] init];
    });
    return manager;
}

- (instancetype)init {
    if (self = [super init]) {
        _nativeModules = [[NSMutableDictionary alloc] init];
        _reactModules = [[NSMutableDictionary alloc] init];
        _stack = [NSMutableArray array];
    }
    return self;
}

- (void)registerNativeModule:(NSString *)moduleName forController:(Class)clazz {
    [self.nativeModules setObject:clazz forKey:moduleName];
}

- (BOOL)hasNativeModule:(NSString *)moduleName {
    return [self.nativeModules objectForKey:moduleName] != nil;
}

- (Class)nativeModuleClassFromName:(NSString *)moduleName {
    return [self.nativeModules objectForKey:moduleName];
}

- (void)registerReactModule:(NSString *)moduleName options:(NSDictionary *)options {
    [self.reactModules setObject:options forKey:moduleName];
}

- (BOOL)hasReactModuleForName:(NSString *)moduleName {
    return [self.reactModules objectForKey:moduleName] != nil;
}

- (NSDictionary *)reactModuleOptionsForKey:(NSString *)moduleName {
    return [self.reactModules objectForKey:moduleName];
}

- (UIViewController *)fetchViewController:(NSString *)pageName params:(NSDictionary * __nullable)params {
    BOOL hasNativeVC = [self hasNativeModule:pageName];
    UIViewController *vc;
    if (hasNativeVC) {
        Class clazz = [self nativeModuleClassFromName:pageName];
        vc = [[clazz alloc] initWithModuleName:pageName props:params];
    } else {
        NSDictionary *options = [self reactModuleOptionsForKey:pageName];
        vc = [[ALCReactViewController alloc] initWithModuleName:pageName options:options];
//        vc.presentationController.delegate = vc;
    }
    return vc;
}

- (UIImage *)fetchImage:(NSDictionary *)json {
  return [RCTConvert UIImage:json];
}

- (void)push:(UIViewController *)vc {
    ALCStackModel *model = [[ALCStackModel alloc] initWithScreenID:vc.screenID];
    if ([self.stack containsObject:model]) {
        NSUInteger index = [self.stack indexOfObject:model];
        ALCStackModel *last = self.stack.lastObject;
        [self.stack removeObjectsInRange:NSMakeRange(index + 1, self.stack.count - (index + 1))];
        if (last.data) {
            [vc didReceiveResultData:last.data type:@"ok"];
        } else {
            [vc didReceiveResultData:@{} type:@"cancel"];
        }
    } else {
        [self.stack addObject:model];
    }
}

- (void)clear {
    [self.stack removeAllObjects];
}

@end
