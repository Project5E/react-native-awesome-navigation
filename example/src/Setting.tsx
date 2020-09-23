import React from 'react'
import {View, Button} from 'react-native'

const Setting = props => {
  return (
    <View style={{flex: 1, justifyContent: 'center'}}>
      <Button
        onPress={() => {
          props.navigator.switchTab(0)
        }}
        title="switch tab"
      />
      <Button
        onPress={() => {
          props.navigator.push('NativeViewController', {
            title: 'Native',
          })
        }}
        title="push native"
      />
      <Button
        onPress={() => {
          props.navigator.push('Detail')
        }}
        title="push detail"
      />
    </View>
  )
}

Setting.navigationItem = {
  title: '设置',
  hideNavigationBar: true,
}

export default Setting
