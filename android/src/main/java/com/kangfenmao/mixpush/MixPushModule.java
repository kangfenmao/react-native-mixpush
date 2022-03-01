package com.kangfenmao.mixpush;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.mixpush.core.GetRegisterIdCallback;
import com.mixpush.core.MixPushClient;
import com.mixpush.core.MixPushPlatform;

@ReactModule(name = MixPushModule.NAME)
public class MixPushModule extends ReactContextBaseJavaModule {
    public static final String NAME = "MixPushModule";
    public static ReactApplicationContext reactContext;
    public static MixPushStatus mixPushStatus = new MixPushStatus();

    public MixPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        MixPushModule.reactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    public static void initMixPush() {
        Log.d("[MixPush]", "initMixPush setPushReceiver");
        MixPushClient.getInstance().setPushReceiver(new MixPushReceiver());
    }

    @ReactMethod
    public void init() {
      MixPushClient.getInstance().register(reactContext);
      synchronized(MixPushModule.mixPushStatus) {
          MixPushModule.mixPushStatus.setInitialized(true);
          MixPushModule.mixPushStatus.notifyAll();
      }
    }

    @ReactMethod
    public void getRegId(Promise promise) {
      MixPushClient.getInstance().getRegisterId(this.getReactApplicationContext(), new GetRegisterIdCallback() {
        @Override
        public void callback(@Nullable MixPushPlatform platform) {
          promise.resolve(platform.getRegId());
        }
      });
    }

    @ReactMethod
    public void getPlatform(Promise promise) {
      MixPushClient.getInstance().getRegisterId(this.getReactApplicationContext(), new GetRegisterIdCallback() {
        @Override
        public void callback(@Nullable MixPushPlatform platform) {
          promise.resolve(platform.getPlatformName());
        }
      });
    }

    @ReactMethod void getBadgeNumber(Promise promise) {
      promise.resolve(0);
    }

    @ReactMethod void setBadgeNumber(Integer no, Promise promise) {
      promise.resolve(no);
    }
}
