package com.kangfenmao.mixpush;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.mixpush.core.MixPushMessage;
import com.mixpush.core.MixPushPlatform;

public class MixPushReceiver extends com.mixpush.core.MixPushReceiver {
  @Override
  public void onRegisterSucceed(Context context, MixPushPlatform mixPushPlatform) {
    Log.d("[MixPush]", "onRegisterSucceed");
    WritableMap params = Arguments.createMap();
    params.putString("platform", mixPushPlatform.getPlatformName());
    params.putString("regid", mixPushPlatform.getRegId());
    MixPushHelper.sendEvent("MIXPUSH_ON_REGISTER_SUCCEED", params);
  }

  @Override
  public void onNotificationMessageClicked(Context context, MixPushMessage message) {
    Log.d("[MixPush]", "onNotificationMessageClicked");
    MixPushHelper.launchApp(context);
    MixPushHelper.sendMessageEvent("MIXPUSH_ON_MESSAGE_CLICKED", message);
  }

  @Override
  public void onNotificationMessageArrived(Context context, MixPushMessage message) {
    Log.d("[MixPush]", "onNotificationMessageArrived");
    MixPushHelper.sendMessageEvent("MIXPUSH_ON_MESSAGE_ARRIVED", message);
  }

  @Override
  public void openAppCallback(Context context) {
    Log.d("[MixPush]", "openAppCallback");
  }
}
