import React from 'react'
import {Button, View} from 'react-native'

const TabBar = props => {
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
