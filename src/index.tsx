import { NativeEventEmitter, NativeModules, Platform } from 'react-native'

const LINKING_ERROR =
  `The package 'react-native-mixpush' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n'

type MixPushType = {
  init: () => void
  getRegId: () => Promise<string>
  getPlatform: () => Promise<string>
  getBadgeNumber: () => Promise<number>
  setBadgeNumber: (no: number) => void
}

export const MixPush: MixPushType = NativeModules.MixPushModule
  ? NativeModules.MixPushModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR)
        }
      }
    )

export const MixPushEventEmitter = new NativeEventEmitter(NativeModules.MixPushModule as any)
