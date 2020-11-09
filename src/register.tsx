import {Navigator} from './navigator'
import React, {useEffect} from 'react'
import {AppRegistry} from 'react-native'
import store from './store'
import {router} from './router'
import {
  NavigationBridge,
  EventEmitter,
  NAVIGATION_EVENT,
  COMPONENT_RESULT,
  SCREEN_ID,
  EVENT_TYPE,
  RESULT_DATA,
} from './navigationModule'

export interface NavigationProps {
  navigator: Navigator
  screenID: string
}

interface Props {
  screenID: string
}

const withNavigator = (moduleName: string) => {
  return (WrappedComponent: React.ComponentType<any>) => {
    const FC = (props: Props, ref: React.Ref<React.ComponentType<any>>) => {
      const {screenID} = props
      const navigator = new Navigator(screenID, moduleName)
      store.addNavigator(screenID, navigator)
      useEffect(() => {
        navigator.signalFirstRenderComplete()
        const subscription = EventEmitter.addListener(NAVIGATION_EVENT, data => {
          if (data[SCREEN_ID] === screenID && data[EVENT_TYPE] === COMPONENT_RESULT) {
            navigator.excute(data[RESULT_DATA])
          }
        })
        return () => {
          store.deleteNavigator(navigator.screenID)
          subscription.remove()
        }
      }, [])
      const injected = {
        navigator,
      }
      return <WrappedComponent ref={ref} {...props} {...injected} />
    }
    const REFC = React.forwardRef(FC)
    return REFC
  }
}

export type HOC = (WrappedComponent: React.ComponentType<any>) => React.ComponentType<any>
let wrap: HOC | undefined

export class Register {
  static beforeRegister = (hoc?: HOC) => {
    wrap = hoc
    store.clearNavigator()
    router.clear()
  }

  static registerComponent = (appKey: string, component: any, customPath?: string) => {
    if (customPath) {
      router.addRoutePath(appKey, customPath)
    } else {
      router.addRoutePath(appKey, `/${appKey}`)
    }
    const options = component.navigationItem || {}
    NavigationBridge.registerReactComponent(appKey, options)
    let withComponent: React.ComponentType<any>
    if (wrap) {
      withComponent = wrap(withNavigator(appKey)(component))
    } else {
      withComponent = withNavigator(appKey)(component)
    }
    AppRegistry.registerComponent(appKey, () => withComponent)
  }

  static setRoot = (tree: {[key: string]: string}) => {
    NavigationBridge.setRoot(tree)
  }
}
