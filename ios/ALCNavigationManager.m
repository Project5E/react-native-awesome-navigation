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
#import "ALCConstants.h"

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
        _nativeModules = [NSMutableDictionary dictionary];
        _reactModules  = [NSMutableDictionary dictionary];
        _stacks     = [NSMutableDictionary dictionary];
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
    UIViewController *vc;
    if ([self hasNativeModule:pageName]) {
        Class clazz = [self nativeModuleClassFromName:pageName];
        vc = [[clazz alloc] initWithModuleName:pageName props:params];
    } else if ([self hasReactModuleForName:pageName]) {
        NSDictionary *options = [self reactModuleOptionsForKey:pageName];
        vc = [[ALCReactViewController alloc] initWithModuleName:pageName props:params options:options];
    } else {
        return nil;
    }
    return vc;
}

- (void)push:(UINavigationController *)nav vc:(UIViewController *)vc {
    ALCStackModel *model = [[ALCStackModel alloc] initWithScreenID:vc.screenID];
    NSMutableArray *stack = [self.stacks valueForKey:nav.screenID];
    if (!stack) {
        stack = [NSMutableArray array];
        [self.stacks setValue:stack forKey:nav.screenID];
    }
    if (![stack containsObject:model]) {
        [stack addObject:model];
    } else if (stack.count > 1) {
       NSUInteger index = [stack indexOfObject:model];
       ALCStackModel *last = stack.lastObject;
        NSRange range = NSMakeRange(index + 1, stack.count - (index + 1));
       [stack removeObjectsInRange:range];
       if (last.data) {
           [vc didReceiveResultData:last.data type:RESULT_TYPE_OK];
       } else {
           [vc didReceiveResultData:@{} type:RESULT_TYPE_CANCEL];
       }
    }
}

- (void)removePresentStack:(UINavigationController *)nav {
    [self.stacks removeObjectForKey:nav.screenID];
}

- (void)clearStack {
    [self.stacks removeAllObjects];
}

@end
