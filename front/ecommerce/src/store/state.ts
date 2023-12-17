import { atom } from 'recoil';
import { AllCategoriesByDepthResponseDTO } from '../../models/src/model/all-categories-by-depth-response-dto';

export const categoriesState = atom<AllCategoriesByDepthResponseDTO[]>({
  key: 'categoriesState',
  default: [],
});

export const isLoggedInState = atom<boolean>({
  key: 'isLoggedInState',
  default: false,
});
