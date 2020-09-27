import {NativeModules, NativeEventEmitter} from 'react-native'

const NavigationBridge = NativeModules.ALCNavigationBridge
const NavigationConstants = NativeModules.ALCConstants

const EventEmitter = new NativeEventEmitter(NavigationBridge)

export const NAVIGATION_EVENT = NavigationConstants.NAVIGATION_EVENT

export const EVENT_TYPE = NavigationConstants.EVENT_TYPE
export const RECLICK_TAB = NavigationConstants.RECLICK_TAB
export const VIEW_DID_APPEAR = NavigationConstants.VIEW_DID_APPEAR
export const VIEW_DID_DISAPPEAR = NavigationConstants.VIEW_DID_DISAPPEAR
export const COMPONENT_RESULT = NavigationConstants.COMPONENT_RESULT

export const RESULT_TYPE = NavigationConstants.RESULT_TYPE
export const RESULT_TYPE_OK = NavigationConstants.RESULT_TYPE_OK
export const RESULT_TYPE_CANCEL = NavigationConstants.RESULT_TYPE_CANCEL
export const RESULT_DATA = NavigationConstants.RESULT_DATA

export const SCREEN_ID = NavigationConstants.SCREEN_ID

export {EventEmitter, NavigationBridge}
