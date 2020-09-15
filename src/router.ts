import {Navigator} from './Navigator'
import {Linking} from 'react-native'

let active = 0
const configs = new Map<string, string>()

class Router {
  private uriPrefix?: string

  addRouteConfig(moduleName: string, routePath: string) {
    configs.set(routePath, moduleName)
  }

  clear = () => {
    active = 0
    configs.clear()
  }

  open = async (path: string) => {
    if (!path) {
      return
    }

    const [pathNameToResolve, queryString] = path.split('?')

    const moduleName = configs.get(pathNameToResolve)

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
    if (moduleName && navigator) {
      navigator?.push(moduleName, queryParams)
    }
  }

  activate = (uriPrefix: string) => {
    if (!uriPrefix) {
      throw new Error('must pass `uriPrefix` when activate router.')
    }
    if (active === 0) {
      this.uriPrefix = uriPrefix
      Linking.addEventListener('url', this.routeEventHandler)
    }
    active++
  }

  inactivate = () => {
    active--
    if (active === 0) {
      Linking.removeEventListener('url', this.routeEventHandler)
    }
    if (active < 0) {
      active = 0
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
