import {NativeModules, NativeEventEmitter} from 'react-native'

const NavigationBridge = NativeModules.ALCNavigationBridge
const NavigationConstants = NativeModules.ALCConstants

const EventEmitter = new NativeEventEmitter(NavigationBridge)

export const NAVIGATION_EVENT = NavigationConstants.NAVIGATION_EVENT // 事件通知

export const EVENT_TYPE = NavigationConstants.EVENT_TYPE // 通知类型
export const RECLICK_TAB = NavigationConstants.RECLICK_TAB // 重复点击tabbar item
export const VIEW_DID_APPEAR = NavigationConstants.VIEW_DID_APPEAR // 页面已出现
export const VIEW_DID_DISAPPEAR = NavigationConstants.VIEW_DID_DISAPPEAR // 页面已消失
export const COMPONENT_RESULT = NavigationConstants.COMPONENT_RESULT // 接收传值结果

export const RESULT_TYPE = NavigationConstants.RESULT_TYPE // 传值结果类型
export const RESULT_TYPE_OK = NavigationConstants.RESULT_TYPE_OK // 传值结果类型 ok
export const RESULT_TYPE_CANCEL = NavigationConstants.RESULT_TYPE_CANCEL // 传值结果类型 cancel
export const RESULT_DATA = NavigationConstants.RESULT_DATA // 传值结果数据

export const SCREEN_ID = NavigationConstants.SCREEN_ID // 页面ID

export {EventEmitter, NavigationBridge}
