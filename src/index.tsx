import {Navigator} from './Navigator'
import React, {useEffect} from 'react'
import {NativeModules, AppRegistry, NativeEventEmitter} from 'react-native'
import store from './Store'
import {router} from './router'

const NavigationBridge = NativeModules.ALCNavigationBridge
const EventEmitter = new NativeEventEmitter(NavigationBridge)

interface Props {
  screenID: string
}

const withNavigator = (moduleName: string) => {
  return (WrappedComponent: React.ComponentType<any>) => {
    const FC = (props: Props, ref: React.Ref<React.ComponentType<any>>) => {
      const {screenID} = props
      const navigator = store.getNavigator(screenID) || new Navigator(screenID, moduleName)
      store.addNavigator(screenID, navigator)
      useEffect(() => {
        const subscription = EventEmitter.addListener('NavigationEvent', data => {
          if (data.screen_id === screenID && data.event === 'component_result') {
            if (data.result_type === 'cancel') {
              navigator.unmount()
            } else {
              navigator.excute(data.result_data)
            }
          }
        })
        return () => {
          // navigator.unmount()
          store.removeNavigator(navigator.screenID)
          subscription.remove()
        }
      }, [])
      const injected = {
        navigator,
      }
      return <WrappedComponent ref={ref} {...props} {...injected} />
    }
    const FREC = React.forwardRef(FC)
    return FREC
  }
}

export const beforeRegister = () => {
  store.clear()
}

export const registerComponent = (appKey: string, component: any, routeConfig?: string) => {
  if (routeConfig) {
    router.addRouteConfig(appKey, routeConfig)
  }
  const options = component.navigationItem || {}
  NavigationBridge.registerReactComponent(appKey, options)
  const withComponent = withNavigator(appKey)(component)
  AppRegistry.registerComponent(appKey, () => withComponent)
}

export const setRoot = (tree: {[key: string]: string}) => {
  NavigationBridge.setRoot(tree)
}

export const setStyles = (styles: {[key: string]: string}) => {
  NavigationBridge.setStyles(styles)
}

export {Navigator, router}
