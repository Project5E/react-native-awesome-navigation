import {Navigator} from './navigator'
import {Linking} from 'react-native'

let active = false
const configs = new Map<string, string>()

class Router {
  static uriPrefix?: string

  static open = async (path: string) => {
    if (!path) {
      return
    }
    path = path.replace(Router.uriPrefix!, '')
    if (!path.startsWith('/')) {
      path = `/${path}`
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

  addRoutePath(moduleName: string, routePath: string) {
    configs.set(routePath, moduleName)
  }

  clear = () => {
    active = false
    configs.clear()
  }

  activate = (uriPrefix: string) => {
    if (!uriPrefix) {
      throw new Error('must pass `uriPrefix` when activate router.')
    }
    if (!active) {
      Router.uriPrefix = uriPrefix
      Linking.addEventListener('url', this.routeEventHandler)
      active = !active
    }
  }

  inactivate = () => {
    if (active) {
      Router.uriPrefix = undefined
      Linking.removeEventListener('url', this.routeEventHandler)
      active = !active
    }
  }

  private readonly routeEventHandler = (event: {url: string}) => {
    Router.open(event.url)
  }
}

const router = new Router()
export {router, Router}
