import React, { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';

interface Props {}

interface Header {
  title: string;
}

export const withNavigationBar = (Component: React.Component) => {
  const newComponent = (props: Props) => {
    const [header, setHeader] = useState<Header | undefined>(undefined);
    return (
      <View style={{ flex: 1 }}>
        <View style={styles.header}>
          <Text> {header?.title ?? ''}</Text>
        </View>
        <Component {...props} setHeader={setHeader} />
      </View>
    );
  };
  newComponent.navigationItem = Component.navigationItem;
  return newComponent;
};

const styles = StyleSheet.create({
  header: {
    height: 64,
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
