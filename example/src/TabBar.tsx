import React, {useEffect} from 'react'
import {BackHandler, Button, Platform, View} from 'react-native'

const TabBar = props => {
  useEffect(() => {
    let backHandler
    if (Platform.OS === 'android') {
      backHandler = () => {
        if (props.navigator.currentTab !== 0) {
          props.navigator.switchTab(0)
        } else {
          BackHandler.exitApp()
        }
        return true
      }
      BackHandler.addEventListener('hardwareBackPress', backHandler)
    }
    return () => {
      if (Platform.OS === 'android') {
        BackHandler.removeEventListener('hardwareBackPress', backHandler)
      }
    }
  })
  return (
    <View style={{backgroundColor: 'yellow', flex: 1, flexDirection: 'row', justifyContent: 'center'}}>
      <Button
        onPress={() => {
          props.navigator.switchTab(0)
        }}
        title={props.tabs[0].title}
      />
      <Button
        onPress={() => {
          props.navigator.switchTab(1)
        }}
        title={props.tabs[1].title}
      />
    </View>
  )
}

export default TabBar
