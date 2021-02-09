import React, {useCallback} from 'react'
import {Button, View} from 'react-native'
import {useVisibleEffect} from 'react-native-awesome-navigation'

const Setting = props => {
  useVisibleEffect(
    props.screenID,
    useCallback(() => {
      console.log(`${props.screenID} is visible`)
      return () => {
        console.log(`${props.screenID} is gone`)
      }
    }, []),
  )
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
