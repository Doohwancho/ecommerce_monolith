import { atom } from 'recoil';

export const sessionIdState = atom<string>({
  key: 'sessionIdState',
  default: '',
});