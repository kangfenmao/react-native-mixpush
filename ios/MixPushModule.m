#import "MixPushModule.h"

NSData *deviceToken;
NSDictionary *currentNotification;

@implementation MixPushModule
{
    bool hasListeners;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"MIXPUSH_ON_REGISTER_SUCCEED",
             @"MIXPUSH_ON_MESSAGE_CLICKED",
             @"MIXPUSH_ON_MESSAGE_ARRIVED"];
}

-(void)startObserving {
    hasListeners = YES;

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleRemoteNotificationsRegistered:)
                                                 name:@"RemoteNotificationsRegistered"
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleRemoteNotificationReceived:)
                                                 name:@"RemoteNotificationReceived"
                                               object:nil];

    if (deviceToken) {
        [MixPushModule didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
    }

    if (currentNotification) {
        [MixPushModule didReceiveRemoteNotification:currentNotification];
    }
}

-(void)stopObserving {
    hasListeners = NO;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

+ (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)token
{
    NSMutableString *hexString = [NSMutableString string];
    NSUInteger deviceTokenLength = token.length;
    const unsigned char *bytes = token.bytes;
    for (NSUInteger i = 0; i < deviceTokenLength; i++) [hexString appendFormat:@"%02x", bytes[i]];
    deviceToken = token;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"RemoteNotificationsRegistered"
                                                        object:self
                                                      userInfo:@{@"deviceToken" : token}];
}

+ (void)didReceiveRemoteNotification:(NSDictionary *)notification
{
    currentNotification = notification;
    NSDictionary *userInfo = @{@"notification": notification};
    [[NSNotificationCenter defaultCenter] postNotificationName:@"RemoteNotificationReceived" object:self userInfo:userInfo];
}

- (void)handleRemoteNotificationsRegistered:(NSNotification *)notification
{
    NSString *regid = [self deviceTokenToString:notification.userInfo[@"deviceToken"]];
    [self sendEventWithName:@"MIXPUSH_ON_REGISTER_SUCCEED" body:@{@"platform": @"ios", @"regid": regid}];
}

- (void)handleRemoteNotificationReceived:(NSNotification *)notification
{
    NSMutableDictionary *remoteNotification = [NSMutableDictionary dictionaryWithDictionary:notification.userInfo[@"notification"]];
    NSMutableDictionary *message = [NSMutableDictionary dictionaryWithDictionary:@{}];

    message[@"title"] = remoteNotification[@"aps"][@"alert"] ? remoteNotification[@"aps"][@"alert"]: @"";
    message[@"subtitle"] = remoteNotification[@"aps"][@"subtitle"] ? remoteNotification[@"aps"][@"subtitle"] : @"";
    message[@"description"] = remoteNotification[@"aps"][@"body"] ? remoteNotification[@"aps"][@"body"] : @"";
    message[@"platform"] = @"ios";
    message[@"payload"] = remoteNotification[@"payload"] ? remoteNotification[@"payload"] : @{};
    message[@"remote"] = @YES;
    message[@"isPassThrough"] = remoteNotification[@"aps"][@"content-available"] ? @YES : @NO;

    [self sendEventWithName:@"MIXPUSH_ON_MESSAGE_CLICKED" body:message];
}

- (NSString *)deviceTokenToString:(NSData *)token {
    NSMutableString *hexString = [NSMutableString string];
    NSUInteger deviceTokenLength = token.length;
    const unsigned char *bytes = token.bytes;
    for (NSUInteger i = 0; i < deviceTokenLength; i++) [hexString appendFormat:@"%02x", bytes[i]];
    return [hexString copy];
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(getRegId
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
    resolve([self deviceTokenToString:deviceToken]);
}

RCT_EXPORT_METHOD(getPlatform
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
    resolve(@"ios");
}

RCT_EXPORT_METHOD(init
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
    resolve(@{@"message": @"MixPush 初始化完成", @"code": @200});
}

RCT_EXPORT_METHOD(getBadgeNumber
                  :(RCTPromiseResolveBlock)resolve
                  :(RCTPromiseRejectBlock)reject) {
    NSInteger number = [UIApplication sharedApplication].applicationIconBadgeNumber;
    resolve(@(number));
}

RCT_EXPORT_METHOD(setBadgeNumber:(NSInteger)number)
{
    [UIApplication sharedApplication].applicationIconBadgeNumber = number;
}

@end
