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

function useVisible(screenID: string) {
  const [visible, setVisible] = useState(false)
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
