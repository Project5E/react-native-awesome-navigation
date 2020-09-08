import { NativeModules } from 'react-native';

type PureNavigationType = {
  multiply(a: number, b: number): Promise<number>;
};

const { PureNavigation } = NativeModules;

export default PureNavigation as PureNavigationType;
