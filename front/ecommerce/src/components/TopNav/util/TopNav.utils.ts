import { AllCategoriesByDepthResponseDTO } from '../../../../models/src/model/all-categories-by-depth-response-dto';

const throttle = (callback: (...args: any[]) => void, waitTime: number) => {
  let timeId: ReturnType<typeof setTimeout> | null = null;
  return (element: any) => {
    if (timeId) {
      return (timeId = setTimeout(() => {
        callback.call(this, element);
        timeId = null;
      }, waitTime));
    }
  };
};

const getUniqueTopCategories = (categories: AllCategoriesByDepthResponseDTO[]) => {
  const uniqueCategories = new Map();

  categories?.forEach(category => {
    if (!uniqueCategories.has(category.topCategoryId)) {
      uniqueCategories.set(category.topCategoryId, category);
    }
  });

  return Array.from(uniqueCategories.values());
}

const filterCategoriesForTopId = (categories: AllCategoriesByDepthResponseDTO[], topCategoryId: number) => {
  return categories.filter(category => category.topCategoryId === topCategoryId);
};


export { throttle, getUniqueTopCategories, filterCategoriesForTopId };