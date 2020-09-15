import {NativeModules} from 'react-native'
import store from './Store'

const NavigationBridge = NativeModules.ALCNavigationBridge

export class Navigator {
  static dispatch = (action: string, component?: string, options?: any) => {
    NavigationBridge.dispatch(action, component, options)
  }

  static get = (screenID: string): Navigator | undefined => {
    console.warn(screenID)

    return store.getNavigator(screenID.screenID)
  }

  static async current() {
    const routeID = await Navigator.currentRoute()
    return Navigator.get(routeID)
  }

  static async currentRoute() {
    return await NavigationBridge.currentRoute()
  }

  resultListener?(data?: any): void

  // eslint-disable-next-line @typescript-eslint/member-ordering
  constructor(screenID: string, moduleName: string) {
    screenID = screenID
    moduleName = moduleName
  }

  waitResult() {
    return new Promise(resolve => {
      const listener = (data: any) => {
        resolve(['ok', data])
        this.resultListener = undefined
      }
      listener.cancel = () => {
        resolve(['cancel', null])
        this.resultListener = undefined
      }
      this.resultListener = listener
    })
  }

  excute = (data: any) => {
    if (this.resultListener) {
      this.resultListener(data)
    }
  }

  unmount = () => {
    if (this.resultListener) {
      this.resultListener.cancel()
    }
  }

  setResult = (data: any) => {
    NavigationBridge.setResult(data)
  }

  push = async (component: string, options?: any) => {
    Navigator.dispatch('push', component, options)
    return await this.waitResult()
  }

  pop = () => {
    Navigator.dispatch('pop')
  }

  popToRoot = () => {
    Navigator.dispatch('popToRoot')
  }

  present = (component: string, options?: any) => {
    Navigator.dispatch('present', component, options)
  }

  dismiss = () => {
    Navigator.dispatch('dismiss')
  }

  switchTab = (index: number) => {
    Navigator.dispatch('switchTab', undefined, index)
  }
}
