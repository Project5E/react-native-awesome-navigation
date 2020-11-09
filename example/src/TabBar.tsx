import React from 'react'
import {View, Button} from 'react-native'

const TabBar = props => {
  return (
    <View style={{backgroundColor: 'yellow', flex: 1, flexDirection: 'row', justifyContent: 'center'}}>
      <Button
        onPress={() => {
          props.navigator.switchTab(0)
        }}
        title="1111"
      />
      <Button
        onPress={() => {
          props.navigator.switchTab(1)
        }}
        title="2222"
      />
    </View>
  )
}

export default TabBar
