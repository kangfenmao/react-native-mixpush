# react-native-mixpush

React Naitve push for iOS and Android

## Installation

```sh
npm install @kangfenmao/react-native-mixpush
```

## iOS

在 AppDelegate.m 中添加 `UNUserNotificationCenterDelegate`

```objectivec
#import <UserNotifications/UserNotifications.h>
#import <UserNotifications/UNUserNotificationCenter.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate, RCTBridgeDelegate, UNUserNotificationCenterDelegate>
```

在AppDelegate.md 中添加

```objectivec
#import <MixPushModule.h>

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  // APNs
  [self registerRemoteNotification];

  // Define UNUserNotificationCenter
  UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
  center.delegate = self;
  // ...
}

- (void)registerRemoteNotification {
  UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
  center.delegate = self;[center requestAuthorizationWithOptions:(UNAuthorizationOptionBadge | UNAuthorizationOptionSound | UNAuthorizationOptionAlert | UNAuthorizationOptionCarPlay) completionHandler:^(BOOL granted, NSError *_Nullable error) {
    if (!error) {
      NSLog(@"request authorization succeeded!");
    }
  }];
  [[UIApplication sharedApplication] registerForRemoteNotifications];
}

// Called when a notification is delivered to a foreground app.
-(void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler
{
  completionHandler(UNNotificationPresentationOptionSound | UNNotificationPresentationOptionAlert | UNNotificationPresentationOptionBadge);
}

// Required for the register event.
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  [MixPushModule didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

// Required for the registrationError event.
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
  NSLog(@">>>[DeviceToken Error]:%@", [error localizedDescription]);
}

// Required for the notification event. You must call the completion handler after handling the remote notification.
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
  [MixPushModule didReceiveRemoteNotification:userInfo];
}
```

## Android

在 android/build.gradle 中添加

```gradle
buildscript {
  repositories {
    maven {url 'https://developer.huawei.com/repo/'}
  }
  dependencies {
    classpath 'com.huawei.agconnect:agcp:1.4.2.300'
  }
}

allprojects {
  repositories {
    maven {
      maven { url 'http://developer.huawei.com/repo/' }
    }
  }
}
```

如果有华为推送需要将 agconnect-services.json 文件放到 android/app 目录下

在 MainApplication.java 中添加

```java
import com.kangfenmao.mixpush.MixPushModule;

@Override
public void onCreate() {
  // ...
  MixPushModule.initMixPush();
}
```

## Usage
```js
import * as React from 'react'
import { MixPush, MixPushEventEmitter } from '@kangfenmao/react-native-mixpush'
import { useEffect, useState } from 'react'
import { Alert, Button, Clipboard, StyleSheet, Text, View } from 'react-native'

export default function App() {
  const [platform, setPlatform] = useState('')
  const [regId, setRegId] = useState('')

  useEffect(() => {
    MixPush.init()
  }, [])

  useEffect(() => {
    const listeners = [
      MixPushEventEmitter.addListener('MIXPUSH_ON_REGISTER_SUCCEED', data => {
        console.log(data)
        setPlatform(data.platform)
        setRegId(data.regid)
      }),
      MixPushEventEmitter.addListener('MIXPUSH_ON_MESSAGE_CLICKED', message => {
        console.log(message)
        setTimeout(() => Alert.alert('消息点击', JSON.stringify(message)), 1000)
      }),
      MixPushEventEmitter.addListener('MIXPUSH_ON_MESSAGE_ARRIVED', message => {
        console.log(message)
        Alert.alert('消息到达', JSON.stringify(message))
      })
    ]
    return () => listeners.forEach(e => e.remove())
  }, [])

  const onCopyRegId = () => {
    Clipboard.setString(regId)
    Alert.alert('RegId Copied')
  }

  const getRegId = async () => {
    const id = await MixPush.getRegId()
    Alert.alert('注册ID', id)
    setRegId(id)
  }

  const getPlatform = async () => {
    const platformName = await MixPush.getPlatform()
    Alert.alert('平台名', platformName)
  }

  const getBadgeNumber = async () => {
    const badgeNumber = await MixPush.getBadgeNumber()
    console.log(badgeNumber)
    Alert.alert('角标数', String(badgeNumber))
  }

  const setBadgeNumber = async (no: number) => {
    MixPush.setBadgeNumber(no)
    Alert.alert('设置成功', String(no))
  }

  return (
    <View style={styles.container}>
      <Text>Platform: {platform}</Text>
      <Text onPress={onCopyRegId}>RegID: {regId}</Text>
      <Button title="获取注册ID" onPress={getRegId} />
      <Button title="获取平台名" onPress={getPlatform} />
      <Button title="获取角标数" onPress={getBadgeNumber} />
      <Button title="设置角标数" onPress={() => setBadgeNumber(10)} />
      <Button title="清除角标数" onPress={() => setBadgeNumber(0)} />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 20,
    justifyContent: 'center'
  }
})
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
