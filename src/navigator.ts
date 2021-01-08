import store from './store'
import {NavigationBridge} from './navigationModule'

interface ResultListener {
  execute(data: any): void
}

interface Route {
  screenID: string
}

interface PresentOption {
  isFullScreen?: boolean // 仅iOS有效 是否全屏Present
  isTransparency?: boolean // 背景是否透明
  animated?: boolean // 是否有动画
  isTabBarPresented?: boolean // 仅iOS有效 是否从自定义TabBar Prensent
}

const defaultPresentOption: PresentOption = {
  isFullScreen: false,
  isTransparency: false,
  animated: true,
  isTabBarPresented: false,
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

  push = async (component: string, params?: any): Promise<any> => {
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

  present = async (
    component: string,
    options = {},
    presentOption?: PresentOption
  ): Promise<any> => {
    Navigator.dispatch(this.screenID, 'present', component, {
      ...options,
      ...defaultPresentOption,
      ...presentOption,
    })
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

  /**
   *
   * @param animated 仅作用于iOS
   */
  dismiss = async (animated = true) => {
    await Navigator.dispatch(this.screenID, 'dismiss', undefined, {animated})
  }

  switchTab(index: number) {
    return Navigator.dispatch(this.screenID, 'switchTab', undefined, {index})
  }

  signalFirstRenderComplete() {
    NavigationBridge.signalFirstRenderComplete(this.screenID)
  }
}
