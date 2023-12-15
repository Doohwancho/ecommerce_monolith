import { AllCategoriesByDepthResponseDTO } from 'model';

export interface ModalProps {
  setMenModalOn: (value: boolean) => void;
  categories: AllCategoriesByDepthResponseDTO[];
}

export interface LowCategory {
  id: number | string;
  name: string;
}

export interface CategoryGroupProps {
  midCategoryName: string;
  lowCategories: LowCategory[];
}

