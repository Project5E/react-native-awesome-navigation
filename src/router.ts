import {Navigator} from './navigator'
import {Linking} from 'react-native'

let active = false
const configs = new Map<string, string>()
let hasHandleInitialURL = false

class Router {
  static uriPrefix?: string

  static open = async (path: string) => {
    if (!path) {
      return
    }
    path = path.replace(Router.uriPrefix, '')
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
        if (value.includes('{') && value.includes('}')) {
          nextResult[key] = JSON.parse(value)
        } else {
          nextResult[key] = value
        }
        return nextResult
      }
      return result
    }, {})
    const navigator = await Navigator.current()
    if (navigator) {
      navigator.push(moduleName, queryParams)
    }
  }

  addRoutePath(moduleName: string, routePath: string) {
    configs.set(routePath, moduleName)
  }

  clear = () => {
    this.inactivate()
    configs.clear()
  }

  activate = (uriPrefix: string) => {
    if (!uriPrefix) {
      throw new Error('must pass `uriPrefix` when activate router.')
    }
    if (!active) {
      Router.uriPrefix = uriPrefix
      if (!hasHandleInitialURL) {
        hasHandleInitialURL = true
        Linking.getInitialURL()
          .then(url => {
            if (url) {
              Router.open(url)
            }
          })
          .catch(err => console.error('An error occurred', err))
      }
      Linking.addEventListener('url', this.routeEventHandler)
      active = true
    }
  }

  inactivate = () => {
    if (active) {
      Router.uriPrefix = undefined
      Linking.removeEventListener('url', this.routeEventHandler)
      active = false
    }
  }

  private readonly routeEventHandler = (event: {url: string}) => {
    Router.open(event.url)
  }
}

const router = new Router()
export {router, Router}
