import React from 'react';
import { View, Button } from 'react-native';

const Setting = (props) => {
  return (
    <View style={{ flex: 1, justifyContent: 'center' }}>
      <Button
        title="switch tab"
        onPress={() => {
          props.navigator.switchTab(0);
        }}
      />
      <Button
        title="push native"
        onPress={() => {
          props.navigator.push('NativeViewController', {
            title: 'Native',
          });
        }}
      />
      <Button
        title="push detail"
        onPress={() => {
          props.navigator.push('Detail');
        }}
      />
    </View>
  );
};

Setting.navigationItem = {
  hideNavigationBar: true,
};

export default Setting;
