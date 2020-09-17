import React, {useEffect} from 'react'
import {View, Button} from 'react-native'
import {Navigator, router, Router} from 'react-native-pure-navigation'

const Home = props => {
  useEffect(() => {
    router.activate('hbd://')
  }, [])
  return (
    <View style={{flex: 1, justifyContent: 'center'}}>
      <Button
        onPress={async () => {
          const resp = await Navigator.currentRoute()
          console.warn(resp)
          // Router.open('/home')
        }}
        title="log route"
      />
      <Button
        onPress={async () => {
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
        onPress={async () => {
          const resp = await props.navigator.push('NativeViewController', {
            title: 'Native',
          })
          console.warn(resp)
        }}
        title="push native"
      />

      <Button
        onPress={() => {
          props.navigator.present('Present')
        }}
        title="present"
      />
    </View>
  )
}

Home.navigationItem = {
  title: 'Home',
  hideNavigationBar: false,
}

export default Home
