import React from 'react';
import { View, Button } from 'react-native';

const NoNavigationBar = (props) => {
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Button
        title="push detail and hide bar"
        onPress={() => {
          props.navigator.push('NoNavigationBar');
        }}
      />
      <Button
        title="push detail"
        onPress={() => {
          props.navigator.push('Detail');
        }}
      />
      <Button
        title="pop"
        onPress={() => {
          props.navigator.pop();
        }}
      />
      <Button
        title="pop to root"
        onPress={() => {
          props.navigator.popToRoot();
        }}
      />
    </View>
  );
};

NoNavigationBar.navigationItem = {
  title: 'Detail',
  hideNavigationBar: true,
};

export default NoNavigationBar;
