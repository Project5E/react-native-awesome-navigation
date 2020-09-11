import { NativeModules } from 'react-native';

const NavigationBridge = NativeModules.ALCNavigationBridge;

export class Navigator {
  screenID: string;
  moduleName: string;
  resultListener?(data?: any): void;

  constructor(screenID: string, moduleName: string) {
    this.screenID = screenID;
    this.moduleName = moduleName;
    this.resultListener = undefined;
  }

  waitResult() {
    return new Promise((resolve) => {
      const listener = (data: any) => {
        resolve(['ok', data]);
        this.resultListener = undefined;
      };
      listener.cancel = () => {
        resolve(['cancel', null]);
        this.resultListener = undefined;
      };
      this.resultListener = listener;
    });
  }

  excute = (data: any) => {
    this.resultListener && this.resultListener(data);
  };

  unmount = () => {
    this.resultListener && this.resultListener.cancel();
  };

  setResult = (data: any) => {
    NavigationBridge.setResult(data);
  };

  static dispatch = (action: string, component?: string, options?: any) => {
    NavigationBridge.dispatch(action, component, options);
  };

  push = async (component: string, options: any) => {
    Navigator.dispatch('push', component, options);
    return await this.waitResult();
  };

  pop = () => {
    Navigator.dispatch('pop');
  };

  popToRoot = () => {
    Navigator.dispatch('popToRoot');
  };

  present = (component: string, options: any) => {
    Navigator.dispatch('present', component, options);
  };

  dismiss = () => {
    Navigator.dispatch('dismiss');
  };

  switchTab = (index: number) => {
    Navigator.dispatch('switchTab', undefined, index);
  };
}
