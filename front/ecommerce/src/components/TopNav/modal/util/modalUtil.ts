import { AllCategoriesByDepthResponseDTO } from 'model';

export const groupLowCategories = (categories: AllCategoriesByDepthResponseDTO[]) => {
    const grouped = {};

    categories.forEach(category => {
      const midId = category.midCategoryId;
      if (!grouped[midId]) {
        grouped[midId] = {
          midCategoryName: category.midCategoryName,
          lowCategories: []
        };
      }

      grouped[midId].lowCategories.push({
        id: category.lowCategoryId,
        name: category.lowCategoryName
      });
    });

    return Object.values(grouped);
  }