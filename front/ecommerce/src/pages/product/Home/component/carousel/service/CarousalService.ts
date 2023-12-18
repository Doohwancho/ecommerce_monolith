import { ProductDTO } from "../../../../../../../models/src/model/product-dto";

export const fetchTopRatedProducts = async (): Promise<ProductDTO[]> => { 
    const BASE_URL = import.meta.env.VITE_API_BASE_URL;
    const endpoint = `/products/highestRatings`;
    const fullUrl = BASE_URL + endpoint;

    const response = await fetch(fullUrl, { credentials: 'include' });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
};