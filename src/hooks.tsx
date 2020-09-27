import React, {useState, useEffect, useRef} from 'react'

import {
  COMPONENT_RESULT,
  EventEmitter,
  EVENT_TYPE,
  NAVIGATION_EVENT,
  RECLICK_TAB,
  RESULT_DATA,
  RESULT_TYPE,
  SCREEN_ID,
  VIEW_DID_APPEAR,
  VIEW_DID_DISAPPEAR,
} from './navigationModule'
import {Navigator} from './navigator'

// interface Header {
//   title: string
// }

// export const withNavigationBar = (Component: React.ComponentType<any>) => {
//   const NewComponent = (props: any) => {
//     const [header, setHeader] = useState<Header | undefined>(undefined)
//     return (
//       <View style={styles.container}>
//         <View style={styles.header}>
//           <Text> {header?.title ?? ''}</Text>
//         </View>
//         <Component {...props} setHeader={setHeader} />
//       </View>
//     )
//   }
//   NewComponent.navigationItem = Component.navigationItem
//   return NewComponent
// }

export function useVisible(screenID: string) {
  const navigator = Navigator.get(screenID)
  const [visible, setVisible] = useState(navigator?.visibility === 'visible')

  useEffect(() => {
    const subscription = EventEmitter.addListener(NAVIGATION_EVENT, (data: any) => {
      if (data[SCREEN_ID] === screenID) {
        if (data[EVENT_TYPE] === VIEW_DID_APPEAR) {
          setVisible(true)
        } else if (data[EVENT_TYPE] === VIEW_DID_DISAPPEAR) {
          setVisible(false)
        }
      }
    })

    return () => {
      subscription.remove()
    }
  }, [])

  return visible
}

export function useVisibleEffect(screenID: string, effect: React.EffectCallback) {
  const visible = useVisible(screenID)
  const callback = useRef<(() => void) | void>()

  useEffect(() => {
    if (visible) {
      callback.current = effect()
    }

    return () => {
      if (callback.current) {
        callback.current()
        callback.current = undefined
      }
    }
  }, [effect, visible, screenID])
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

// const styles = StyleSheet.create({
//   container: {flex: 1},
//   header: {
//     height: 64,
//     width: '100%',
//     alignItems: 'center',
//     justifyContent: 'center',
//   },
// })
