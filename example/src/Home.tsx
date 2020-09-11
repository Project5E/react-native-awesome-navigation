import React from 'react';
import { View, Button } from 'react-native';

const Home = (props) => {
  return (
    <View style={{ flex: 1, justifyContent: 'center' }}>
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
        title="push native"
        onPress={async () => {
          const resp = await props.navigator.push('NativeViewController', {
            title: 'Native',
          });
          console.warn(resp);
        }}
      />

      <Button
        title="present"
        onPress={() => {
          props.navigator.present('Present');
        }}
      />
    </View>
  );
};

Home.navigationItem = {
  title: 'Home',
  hideNavigationBar: false,
};

export default Home;
