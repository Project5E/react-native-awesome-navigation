import {Navigator} from './Navigator'

const navigators = new Map<string, Navigator>()

function addNavigator(screenID: string, navigator: Navigator) {
  navigators.set(screenID, navigator)
}

function removeNavigator(screenID: string) {
  navigators.delete(screenID)
}

function getNavigator(screenID: string) {
  return navigators.get(screenID)
}

function clear() {
  navigators.clear()
}

export default {
  addNavigator,
  removeNavigator,
  getNavigator,
  clear,
}
