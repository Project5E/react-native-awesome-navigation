import type {Navigator} from './navigator'

const navigators = new Map<string, Navigator>()

const addNavigator = (screenID: string, navigator: Navigator) => {
  navigators.set(screenID, navigator)
}

const deleteNavigator = (screenID: string) => {
  navigators.delete(screenID)
}

const getNavigator = (screenID: string) => {
  return navigators.get(screenID)
}

const clearNavigator = () => {
  navigators.clear()
}

export default {
  addNavigator,
  deleteNavigator,
  getNavigator,
  clearNavigator,
}
