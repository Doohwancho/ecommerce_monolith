import { atom } from 'recoil';
import { AllCategoriesByDepthResponseDTO } from 'model';

export const categoriesState = atom<AllCategoriesByDepthResponseDTO[]>({
  key: 'categoriesState',
  default: [],
});

export const isLoggedInState = atom<boolean>({
  key: 'isLoggedInState',
  default: false,
});
