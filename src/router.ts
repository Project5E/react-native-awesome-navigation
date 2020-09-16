import {Navigator} from './Navigator'
import {Linking} from 'react-native'

let active = false
const configs = new Map<string, string>()

export interface RouteConfig {
  path: string
}

class Router {
  private uriPrefix?: string

  addRouteConfig(moduleName: string, routePath: string) {
    configs.set(routePath, moduleName)
  }

  clear = () => {
    active = false
    configs.clear()
  }

  open = async (path: string) => {
    if (!path) {
      return
    }
    const [pathName, queryString] = path.split('?')
    const moduleName = configs.get(pathName)
    if (!moduleName) {
      return
    }

    const queryParams = (queryString || '').split('&').reduce((result: any, item: string) => {
      if (item !== '') {
        const nextResult = result || {}
        const [key, value] = item.split('=')
        nextResult[key] = value
        return nextResult
      }
      return result
    }, {})
    const navigator = await Navigator.current()
    if (navigator) {
      navigator?.push(moduleName, queryParams)
    }
  }

  activate = (uriPrefix: string) => {
    if (!uriPrefix) {
      throw new Error('must pass `uriPrefix` when activate router.')
    }
    if (!active) {
      this.uriPrefix = uriPrefix
      Linking.addEventListener('url', this.routeEventHandler)
      active = !active
    }
  }

  inactivate = () => {
    if (active) {
      Linking.removeEventListener('url', this.routeEventHandler)
      active = !active
    }
  }

  private readonly routeEventHandler = (event: {url: string}): void => {
    // console.info(`deeplink: ${event.url}`)
    let path = event.url.replace(this.uriPrefix!, '')
    if (!path.startsWith('/')) {
      path = `/${path}`
    }
    this.open(path)
  }
}

const router = new Router()
export {router}
