import { ProductDetailResponseDTO } from 'model';

export const fetchProductsByProductId = async (productId: number): Promise<ProductDetailResponseDTO[]> => {
    const baseUrl = 'http://127.0.0.1:8080';
    const endpoint = `/products/${productId}`;
    const fullUrl = baseUrl + endpoint;
  
    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };  

