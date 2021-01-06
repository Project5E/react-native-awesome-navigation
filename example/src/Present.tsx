import React, { useCallback } from 'react'
import {View, Button, Text} from 'react-native'
import {
  useVisibleEffect,
} from 'react-native-navigation-5e'

const Present = props => {
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
    <View style={{flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'black', opacity: 0.8}}>
      <Text style={{fontSize: 20}}>This is a presented view</Text>
      <Button
        onPress={async () => {
          const resp = await props.navigator.push('Detail')
          console.warn(resp)
        }}
        title="push detail"
      />
      <Button
        onPress={async () => {
          props.navigator.setResult({'key': 123})
          await props.navigator.dismiss()
          // props.navigator.present('Present')
        }}
        title="dismiss"
      />
    </View>
  )
}

Present.navigationItem = {
  title: 'Present',
  hideNavigationBar: true,
}

export default Present
