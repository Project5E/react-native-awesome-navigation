import type {Navigator} from './navigator'

const navigators = new Map<string, Navigator>()

function addNavigator(screenID: string, navigator: Navigator) {
  navigators.set(screenID, navigator)
}

function deleteNavigator(screenID: string) {
  navigators.delete(screenID)
}

function getNavigator(screenID: string) {
  return navigators.get(screenID)
}

function clearNavigator() {
  navigators.clear()
}

export default {
  addNavigator,
  deleteNavigator,
  getNavigator,
  clearNavigator,
}
