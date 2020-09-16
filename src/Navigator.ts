import {NativeModules} from 'react-native'
import store from './store'

const NavigationBridge = NativeModules.ALCNavigationBridge

interface Route {
  screenID: string
}

export class Navigator {
  static dispatch = (action: string, component?: string, options?: any) => {
    NavigationBridge.dispatch(action, component, options)
  }

  static get = (screenID: string): Navigator | undefined => {
    return store.getNavigator(screenID)
  }

  static async current() {
    const route = await Navigator.currentRoute()
    const navigatior = Navigator.get(route.screenID)
    return navigatior
  }

  static async currentRoute(): Promise<Route> {
    return new Promise(resolve => {
      const route: Route = NavigationBridge.currentRoute()
      resolve(route)
    })
  }

  screenID: string
  moduleName: string
  resultListener?(data?: any): void

  // eslint-disable-next-line @typescript-eslint/member-ordering
  constructor(screenID: string, moduleName: string) {
    this.screenID = screenID
    this.moduleName = moduleName
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

  push = async (component: string, params?: any) => {
    Navigator.dispatch('push', component, params)
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

  // switchTab = (index: number) => {
  //   Navigator.dispatch('switchTab', undefined, index)
  // }
}
