import React, {useCallback} from 'react'
import {View, Button} from 'react-native'
import {useVisibleEffect} from 'react-native-navigation-5e'

const Detail = props => {
  useVisibleEffect(
    props.screenID,
    useCallback(() => {
      console.log(`${props.screenID} is visible`)
      return () => {
        console.log(`${props.screenID} is gone`)
      }
    }, [])
  )
  return (
    <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
      <Button
        onPress={() => {
          props.navigator.push('NoNavigationBar')
        }}
        title="push detail and hide bar"
      />
      <Button
        onPress={async () => {
          const resp = await props.navigator.push('Detail')
          console.warn(resp)
        }}
        title="push detail"
      />
      <Button
        onPress={() => {
          props.navigator.setResult({qwe: 123})
          props.navigator.pop()
        }}
        title="pop"
      />
      <Button
        onPress={() => {
          props.navigator.popToRoot()
        }}
        title="pop to root"
      />
    </View>
  )
}

Detail.navigationItem = {
  title: 'Detail',
  hideNavigationBar: false,
}

export default Detail
