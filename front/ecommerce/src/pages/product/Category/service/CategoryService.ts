// API calls are moved to a separate service file
import { OptionsOptionVariationsResponseDTO, ProductWithOptionsListResponseDTO, ProductWithOptionsDTO } from 'model';

export const fetchCategoryOptions = async (categoryId: number): Promise<OptionsOptionVariationsResponseDTO[]> => {
    const baseUrl = 'http://127.0.0.1:8080';
    const endpoint = `/categories/${categoryId}/options`;
    const fullUrl = baseUrl + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
};

export const fetchProductsWithOptionsByCategoryId = async (categoryId: number): Promise<ProductWithOptionsListResponseDTO> => {
    const baseUrl = 'http://127.0.0.1:8080';
    const endpoint = `/products/category/${categoryId}`;
    const fullUrl = baseUrl + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
};
