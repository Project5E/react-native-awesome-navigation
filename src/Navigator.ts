import store from './store'
import {NavigationBridge} from './NavigationModule'

interface ResultListener {
  execute(data: any): void
  cancel(): void
}

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
    return Navigator.get(route.screenID)
  }

  static async currentRoute(): Promise<Route> {
    return new Promise(resolve => {
      const route: Route = NavigationBridge.currentRoute()
      resolve(route)
    })
  }

  screenID: string
  moduleName: string
  resultListener?: ResultListener

  // eslint-disable-next-line @typescript-eslint/member-ordering
  constructor(screenID: string, moduleName: string) {
    this.screenID = screenID
    this.moduleName = moduleName
  }

  excute = (data: any) => {
    if (this.resultListener) {
      this.resultListener.execute(data)
    }
  }

  cancel = () => {
    if (this.resultListener) {
      this.resultListener.cancel()
    }
  }

  setResult = (data: any) => {
    NavigationBridge.setResult(data)
  }

  push = async (component: string, params?: any) => {
    Navigator.dispatch('push', component, params)
    return new Promise(resolve => {
      const listener = {
        execute: (data: any) => {
          resolve(['ok', data])
          this.resultListener = undefined
        },
        cancel: () => {
          resolve(['cancel', null])
          this.resultListener = undefined
        },
      }
      this.resultListener = listener
    })
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
}
