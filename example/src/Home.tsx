import React, { useEffect } from 'react';
import { View, Button } from 'react-native';
import { Navigator, router } from 'react-native-pure-navigation';

const Home = (props) => {
  useEffect(() => {
    router.activate('hbd://')
  }, [])
  return (
    <View style={{ flex: 1, justifyContent: 'center' }}>
      <Button
        title="log route"
        onPress={async () => {
          const resp = await Navigator.currentRoute()
          console.warn(resp);
        }}
      />
      <Button
        title="push detail and hide bar"
        onPress={async () => {
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
