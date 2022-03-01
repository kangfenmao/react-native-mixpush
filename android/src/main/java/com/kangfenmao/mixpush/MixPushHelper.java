package com.kangfenmao.mixpush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mixpush.core.MixPushMessage;

public class MixPushHelper {
  public static void sendEvent(String eventName, WritableMap params) {
    Log.d("[MixPush]", "sendEvent" + params.toString());

    try {
      MixPushModule.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(eventName, params);
    } catch (Throwable throwable){
      Log.e("[MixPush]", "sendEvent error:" + throwable.getMessage());
    }
  }

  public static void sendMessageEvent(String eventName, MixPushMessage message) {
    Log.d("[MixPush]", "sendMessageEvent wait initialized");

    new Thread() {
      public void run() {
        if (!MixPushModule.mixPushStatus.initialized) {
          synchronized(MixPushModule.mixPushStatus) {
            try {
              MixPushModule.mixPushStatus.wait();
            } catch (InterruptedException e) {
              Log.e("[MixPush]", "MixPushModule.initialized error:" + e.toString());
            }
          }
        }

        Log.d("[MixPush]", "sendMessageEvent" + message.toString());

        WritableMap msg = Arguments.createMap();
        msg.putString("title", message.getTitle());
        msg.putString("description", message.getDescription());
        msg.putString("platform", message.getPlatform());
        msg.putString("payload", message.getPayload());
        msg.putBoolean("remote", true);
        msg.putBoolean("isPassThrough", message.isPassThrough());
        MixPushHelper.sendEvent(eventName, msg);
      }
    }.start();
  }

  public static void launchApp(Context context) {
    try {
      Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      context.startActivity(intent);
    } catch (Throwable throwable) {
      Log.e("[MixPush]", "launchApp error" + throwable.getMessage());
    }
  }
}
