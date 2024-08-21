import { ProductDetailResponseDTO } from '../../../../../models/src/model/product-detail-response-dto';

export const fetchProductsByProductId = async (productId: number): Promise<ProductDetailResponseDTO[]> => {
    const BASE_URL = import.meta.env.VITE_API_BASE_URL;
    const endpoint = `/products/${productId}`;
    const fullUrl = BASE_URL + endpoint;
  
    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  };  

