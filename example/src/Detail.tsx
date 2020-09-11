import React from 'react';
import { View, Button } from 'react-native';

const Detail = (props) => {
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
        onPress={async () => {
          const resp = await props.navigator.push('Detail');
          console.warn(resp);
        }}
      />
      <Button
        title="pop"
        onPress={() => {
          props.navigator.setResult({ qwe: 123 });
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

Detail.navigationItem = {
  title: 'Detail',
  hideNavigationBar: false,
};

export default Detail;
