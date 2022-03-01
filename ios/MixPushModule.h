#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface MixPushModule: RCTEventEmitter <RCTBridgeModule>
+ (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
+ (void)didReceiveRemoteNotification:(NSDictionary *)notification;
@end
