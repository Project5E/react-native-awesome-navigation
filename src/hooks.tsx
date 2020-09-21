import React, {useState, useEffect} from 'react'
import {View, Text, StyleSheet} from 'react-native'
import {
  COMPONENT_RESULT,
  EventEmitter,
  EVENT_TYPE,
  NAVIGATION_EVENT,
  RECLICK_TAB,
  RESULT_DATA,
  RESULT_TYPE,
  SCREEN_ID,
} from './NavigationModule'

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
    const subscription = EventEmitter.addListener(NAVIGATION_EVENT, data => {
      if (data[SCREEN_ID] === screenID && data[EVENT_TYPE] === COMPONENT_RESULT) {
        fn(data[RESULT_TYPE], data[RESULT_DATA])
      }
    })
    return () => {
      subscription.remove()
    }
  }, [screenID, fn])
}

export function useReClick(screenID: string, fn: () => void) {
  useEffect(() => {
    const subscription = EventEmitter.addListener(NAVIGATION_EVENT, data => {
      if (data[SCREEN_ID] === screenID && data[EVENT_TYPE] === RECLICK_TAB) {
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
