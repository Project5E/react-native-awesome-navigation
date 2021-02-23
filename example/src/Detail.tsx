import React, {useCallback, useState} from 'react'
import {Button, Modal, View} from 'react-native'
import {useVisibleEffect} from 'react-native-awesome-navigation'

const Detail = props => {
  useVisibleEffect(
    props.screenID,
    useCallback(() => {
      console.log(`${props.screenID} is visible`)
      return () => {
        console.log(`${props.screenID} is gone`)
      }
    }, []),
  )

  const [modalVisible, setModalVisible] = useState(false)

  return (
    <View style={{flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#ffffffff'}}>
      <Modal
        transparent={true}
        visible={modalVisible}
      >
        <View style={{flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#88888888'}}>
          <Button
            onPress={() => {
              setModalVisible(false)
            }}
            title='modal cancel'
          />
        </View>
      </Modal>
      <Button
        onPress={async () => {
          const resp = await props.navigator.push('Detail')
          console.warn(resp)
        }}
        title='push detail'
      />
      <Button
        onPress={() => {
          props.navigator.push('NoNavigationBar')
        }}
        title='push detail and hide bar'
      />
      <Button
        onPress={() => {
          props.navigator.setResult({qwe: 123})
          props.navigator.pop()
        }}
        title='pop'
      />
      <Button
        onPress={() => {
          props.navigator.popToRoot()
        }}
        title='pop to root'
      />
      <Button
        onPress={() => {
          props.navigator.present('Detail')
        }}
        title='present Detail'
      />
      <Button
        onPress={() => {
          props.navigator.dismiss()
        }}
        title='dismiss'
      />
      <Button
        onPress={async () => {
          const resp = await props.navigator.push('NativeViewController', {
            title: 'Native',
          })
          console.warn(resp)
        }}
        title='push native'
      />
      <Button
        onPress={() => {
          setModalVisible(true)
        }}
        title='showModal'
      />
    </View>
  )
}

Detail.navigationItem = {
  title: 'Detail',
  hideNavigationBar: false,
}

export default Detail
