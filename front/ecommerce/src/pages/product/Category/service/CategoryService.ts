// API calls are moved to a separate service file
import { OptionsOptionVariatonsResponseDTO } from '../../../../../models/src/model/options-option-variatons-response-dto';
import { ProductWithOptionsListResponseDTO } from '../../../../../models/src/model/product-with-options-list-response-dto';


export const fetchCategoryOptions = async (categoryId: string): Promise<OptionsOptionVariatonsResponseDTO[]> => { 
    const BASE_URL = import.meta.env.VITE_API_BASE_URL;
    const endpoint = `/categories/${categoryId}/options`;
    const fullUrl = BASE_URL + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
};

export const fetchProductsWithOptionsByCategoryId = async (categoryId: string): Promise<ProductWithOptionsListResponseDTO> => {
    const BASE_URL = import.meta.env.VITE_API_BASE_URL;
    const endpoint = `/products/category/${categoryId}`;
    const fullUrl = BASE_URL + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
};
