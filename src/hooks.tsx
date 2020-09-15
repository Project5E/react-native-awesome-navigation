import React, {useState} from 'react'
import {View, Text, StyleSheet} from 'react-native'

interface Props {}

interface Header {
  title: string
}

export const withNavigationBar = (Component: React.Component) => {
  const NewComponent = (props: Props) => {
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

const styles = StyleSheet.create({
  container: {flex: 1},
  header: {
    height: 64,
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
})
