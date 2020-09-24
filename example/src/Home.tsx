import React, {useCallback, useEffect} from 'react'
import {View, Button} from 'react-native'
import {
  Navigator,
  router,
  Router,
  setTabBadge,
  useVisibleEffect,
} from 'react-native-pure-navigation'

const Home = props => {
  useEffect(() => {
    // setTabBadge([
    //   {
    //     index: 0,
    //     hidden: false,
    //     dot: true,
    //   },
    //   {
    //     index: 1,
    //     text: '1199',
    //     hidden: false,
    //   },
    // ])
    router.activate('hbd://')
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
    }, [])
  )

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
          props.navigator.present('Present', undefined, true)
        }}
        title="present"
      />
    </View>
  )
}

Home.navigationItem = {
  title: '主页',
  hideNavigationBar: false,
}

export default Home
