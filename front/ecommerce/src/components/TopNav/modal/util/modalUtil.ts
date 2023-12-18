import { AllCategoriesByDepthResponseDTO } from '../../../../../models/src/model/all-categories-by-depth-response-dto';
import { CategoryGroupProps } from '../type/modalTypes';

export const groupLowCategories = (categories: AllCategoriesByDepthResponseDTO[]): CategoryGroupProps[] => {
  const grouped: { [key: string]: CategoryGroupProps } = {};

  categories.forEach(category => {
    const midId = category.midCategoryId;
    if (midId !== undefined) { // Check if midId is not undefined
      if (!grouped[midId]) {
        grouped[midId] = {
          midCategoryName: category.midCategoryName ?? 'Unknown', // Provide a default value for midCategoryName
          lowCategories: []
        };
      }

      if (category.lowCategoryId !== undefined) { // Check if lowCategoryId is not undefined
        grouped[midId].lowCategories.push({
          id: category.lowCategoryId, // Already checked for undefined
          name: category.lowCategoryName ?? 'Unknown' // Provide a default value for lowCategoryName
        });
      }
    }
  });

  return Object.values(grouped);
}
