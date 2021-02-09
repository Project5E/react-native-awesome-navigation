import React, {useCallback, useEffect} from 'react'
import {Alert, Button, Platform, View} from 'react-native'
import {Navigator, router, setTabBadge, useVisibleEffect} from 'react-native-navigation-5e'

const Home = props => {
  useEffect(() => {
    setTabBadge([
      {
        index: 0,
        hidden: false,
        dot: true,
      },
      {
        index: 1,
        text: '1199',
        hidden: false,
      },
    ])
    router.activate('alc://')
    return () => {
      router.inactivate()
    }
  }, [])

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
        onPress={async () => {
          const resp = await Navigator.currentRoute()
          console.warn(resp)
          // Router.open('/home')
        }}
        title='log route'
      />
      <Button
        onPress={async () => {
          props.navigator.push('NoNavigationBar')
        }}
        title='push detail and hide bar'
      />
      <Button
        onPress={async () => {
          const resp = await props.navigator.push('Detail')
          console.warn(resp)
        }}
        title='push detail'
      />
      <Button
        onPress={async () => {
          if (Platform.OS === 'ios') {
            const resp = await props.navigator.push('NativeViewController', {
              title: 'Native',
            })
            console.warn(resp)
          } else {
            Alert.alert('Push Native', 'Android is coming.')
          }
        }}
        title='push native'
      />

      <Button
        onPress={async () => {
          const resp = await props.navigator.present('Present', {}) //{isFullScreen: true, isTransparency: true, animated: false}
          console.log(resp)
        }}
        title='present'
      />
    </View>
  )
}

Home.navigationItem = {
  title: '主页',
  hideNavigationBar: false,
}

export default Home
