import { Navigator } from './Navigator';
import React, { useEffect } from 'react';
import { NativeModules, AppRegistry, NativeEventEmitter } from 'react-native';

const NavigationBridge = NativeModules.ALCNavigationBridge;
const EventEmitter = new NativeEventEmitter(NavigationBridge);

interface Props {
  screenID: string;
}

const withNavigator = (moduleName: string) => {
  return (WrappedComponent: React.ComponentType<any>) => {
    const FC = (props: Props, ref: React.Ref<React.ComponentType<any>>) => {
      const { screenID } = props;
      const navigator = new Navigator(screenID, moduleName);
      useEffect(() => {
        const subscription = EventEmitter.addListener(
          'NavigationEvent',
          (data) => {
            if (
              data.screen_id === screenID &&
              data.event === 'component_result'
            ) {
              if (data.result_type === 'cancel') {
                navigator.unmount();
              } else {
                navigator.excute(data.result_data);
              }
            }
          }
        );
        return () => {
          subscription.remove();
        };
      }, []);
      const injected = {
        navigator,
      };
      return <WrappedComponent ref={ref} {...props} {...injected} />;
    };
    const FREC = React.forwardRef(FC);
    return FREC;
  };
};

export const registerComponent = (appKey: string, component: any) => {
  let options = component.navigationItem || {};
  NavigationBridge.registerReactComponent(appKey, options);
  let withComponent = withNavigator(appKey)(component);
  AppRegistry.registerComponent(appKey, () => withComponent);
};

export const setRoot = (tree: { [key: string]: string }) => {
  NavigationBridge.setRoot(tree);
};

export const setStyles = (styles: { [key: string]: string }) => {
  NavigationBridge.setStyles(styles);
};
