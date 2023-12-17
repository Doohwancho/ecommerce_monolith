import { useQuery } from 'react-query';
import { useRecoilState } from 'recoil';
import { categoriesState } from '../../../store/state';
import { AllCategoriesByDepthResponseDTO } from '../../../../models/src/model/all-categories-by-depth-response-dto';

const fetchCategories = async (): Promise<AllCategoriesByDepthResponseDTO[]> => {
    const response = await fetch('http://127.0.0.1:8080/products/categories', { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };
  
export const useFetchCategories = () => {
  const [categories, setCategories] = useRecoilState<AllCategoriesByDepthResponseDTO[]>(categoriesState);

  //ignore false-positive type error of 'not using data'
  // @ts-expect-error
  const { data, isLoading, error } = useQuery<AllCategoriesByDepthResponseDTO[], Error>('categories', fetchCategories, {
    staleTime: Infinity,
    cacheTime: 1000 * 60 * 60 * 24,
    onSuccess: (data) => {
      setCategories(data);
    }
  });

  return { categories, isLoading, error };
};
