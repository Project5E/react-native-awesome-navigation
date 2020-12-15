import React from 'react'
import {View, Button, Text} from 'react-native'

const Present = props => {
  return (
    <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
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
          await props.navigator.dismiss()
          props.navigator.present('Present')
        }}
        title="dismiss"
      />
    </View>
  )
}

Present.navigationItem = {
  title: 'Present',
  // hideNavigationBar: false,
}

export default Present
