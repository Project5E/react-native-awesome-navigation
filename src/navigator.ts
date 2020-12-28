import store from './store'
import {NavigationBridge} from './navigationModule'

interface ResultListener {
  execute(data: any): void
}

interface Route {
  screenID: string
}

interface PresentOption {
  isFullScreen?: boolean
  isTransparency?: boolean
  animated?: boolean
}

const defaultPresentOption: PresentOption = {
  isFullScreen: false,
  isTransparency: false,
  animated: true,
}

export class Navigator {
  static dispatch = async (screenID: string, action: string, component?: string, options?: any) => {
    await NavigationBridge.dispatch(screenID, action, component, options)
  }

  static get = (screenID: string): Navigator | undefined => {
    return store.getNavigator(screenID)
  }

  static current = async () => {
    const route = await Navigator.currentRoute()
    return Navigator.get(route.screenID)
  }

  static currentRoute = async (): Promise<Route> => {
    return new Promise(resolve => {
      const route: Route = NavigationBridge.currentRoute()
      resolve(route)
    })
  }

  screenID: string
  moduleName: string
  resultListener?: ResultListener

  constructor(screenID: string, moduleName: string) {
    this.screenID = screenID
    this.moduleName = moduleName
  }

  excute = (data: any) => {
    if (this.resultListener) {
      this.resultListener.execute(data)
    }
  }

  setResult = (data: any) => {
    NavigationBridge.setResult(data)
  }

  push = async (component: string, params?: any) => {
    Navigator.dispatch(this.screenID, 'push', component, params)
    return new Promise(resolve => {
      const listener = {
        execute: (data: any) => {
          resolve(data)
          this.resultListener = undefined
        },
      }
      this.resultListener = listener
    })
  }

  pop = () => {
    Navigator.dispatch(this.screenID, 'pop')
  }

  popPages = (count: number) => {
    Navigator.dispatch(this.screenID, 'popPages', undefined, {count})
  }

  popToRoot = () => {
    Navigator.dispatch(this.screenID, 'popToRoot')
  }

  present = (component: string, options = {}, presentOption?: PresentOption) => {
    Navigator.dispatch(this.screenID, 'present', component, {
      ...options,
      ...defaultPresentOption,
      ...presentOption,
    })
  }

  dismiss = async (animated: boolean) => {
    await Navigator.dispatch(this.screenID, 'dismiss', undefined, {animated})
  }

  switchTab(index: number) {
    return Navigator.dispatch(this.screenID, 'switchTab', undefined, {index})
  }

  signalFirstRenderComplete() {
    NavigationBridge.signalFirstRenderComplete(this.screenID)
  }
}
