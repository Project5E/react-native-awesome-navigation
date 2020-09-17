import React, {useState, useEffect} from 'react'
import {View, Text, StyleSheet, NativeEventEmitter, NativeModules} from 'react-native'

const NavigationBridge = NativeModules.ALCNavigationBridge
const EventEmitter = new NativeEventEmitter(NavigationBridge)

interface Header {
  title: string
}

export const withNavigationBar = (Component: React.ComponentType<any>) => {
  const NewComponent = (props: any) => {
    const [header, setHeader] = useState<Header | undefined>(undefined)
    return (
      <View style={styles.container}>
        <View style={styles.header}>
          <Text> {header?.title ?? ''}</Text>
        </View>
        <Component {...props} setHeader={setHeader} />
      </View>
    )
  }
  NewComponent.navigationItem = Component.navigationItem
  return NewComponent
}

export function useResult(screenID: string, fn: (type: string, data: any) => void) {
  useEffect(() => {
    const subscription = EventEmitter.addListener('NavigationEvent', data => {
      if (data.screen_id === screenID && data.event === 'component_result') {
        fn(data.result_type, data.result_data)
      }
    })
    return () => {
      subscription.remove()
    }
  }, [screenID, fn])
}

export function useReClick(screenID: string, fn: () => void) {
  useEffect(() => {
    const subscription = EventEmitter.addListener('NavigationEvent', data => {
      if (screenID === data.screen_id && data.event === 'did_select_tab') {
        fn()
      }
    })
    return () => {
      subscription.remove()
    }
  }, [screenID, fn])
}

const styles = StyleSheet.create({
  container: {flex: 1},
  header: {
    height: 64,
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
})
