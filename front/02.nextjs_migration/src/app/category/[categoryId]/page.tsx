import { ProductWithOptionsListVer2ResponseDTO, CategoryOptionsOptionVariationsResponseDTO } from '../../../../models';
import CategoryClientSideComponent from './CategoryClientSideComponent';

interface CategoryPageProps {
    params: {
        categoryId: string;
    };
}

const fallbackProducts: ProductWithOptionsListVer2ResponseDTO = {
    products: []
};

const fallbackOptionsAndVariations: CategoryOptionsOptionVariationsResponseDTO = {
    categoryId: 0,
    categoryName: '',
    options: []
};

const fetchProductsWithOptionsByCategoryId = async (categoryId: string): Promise<ProductWithOptionsListVer2ResponseDTO> => {
    const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
    if (!BASE_URL) {
        console.error('API URL is not configured');
        return fallbackProducts;
    }

    const endpoint = `/products/category/v2/${categoryId}`;
    const fullUrl = BASE_URL + endpoint;

    try {
        const response = await fetch(fullUrl, {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        // Handle non-200 responses
        if (!response.ok) {
            const errorText = await response.text(); // Log the response text
            console.error('Fetch error response:', errorText);
            console.error('Response status:', response.status);
            console.error('Response headers:', response.headers);

            // For 404 (no products found), return empty array instead of throwing
            if (response.status === 404) {
                return fallbackProducts;
            }
            // throw new Error('Network response was not ok');
        }

        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            console.error('Received non-JSON response:', text);
            return fallbackProducts;
            // throw new Error("Received non-JSON response from server");
        }

        const data = await response.json();
        
        // Validate response structure
        if (!data || !Array.isArray(data.products)) {
            console.error('Invalid response structure:', data);
            return fallbackProducts;
        }

        return data;
    } catch (error) {
        console.error("Fetch error:", error);
        return fallbackProducts;
        // throw error;
    }
};

const fetchOptionsAndOptionVariationsByCategoryId = async (categoryId: string): Promise<CategoryOptionsOptionVariationsResponseDTO> => {
    const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
    if (!BASE_URL) {
        console.error('API URL is not configured');
        return fallbackOptionsAndVariations;
    }
    const endpoint = `/categories/${categoryId}/optionVariations`;
    const fullUrl = BASE_URL + endpoint;

    try {
        const response = await fetch(fullUrl, {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        if (!response.ok) {
            const errorText = await response.text(); // Log the response text
            console.error('Fetch error response:', errorText);
            console.error('Response status:', response.status);
            console.error('Response headers:', response.headers);

            // For 404 (category not found), return empty options instead of throwing
            if (response.status === 404) {
                return fallbackOptionsAndVariations;
            }
            return fallbackOptionsAndVariations;
            // throw new Error('Network response was not ok');
        }

        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            console.error('Received non-JSON response:', text);
            return fallbackOptionsAndVariations;
            // throw new Error("Received non-JSON response from server");
        }

        const data = await response.json();
        
        // Validate response structure
        if (!data || typeof data.categoryId === 'undefined') {
            console.error('Invalid response structure:', data);
            return fallbackOptionsAndVariations;
        }

        return data;
    } catch (error) {
        console.error("Fetch error:", error);
        return fallbackOptionsAndVariations;
        // throw error;
    }
};

const CategoryPage = async ({ params }: CategoryPageProps) => {
    const { categoryId } = params;

    try {
        // Fetch both data sources concurrently
        const [initialProducts, optionsAndOptionVariations] = await Promise.all([
            fetchProductsWithOptionsByCategoryId(categoryId),
            fetchOptionsAndOptionVariationsByCategoryId(categoryId)
        ]);

        // Even if both fetches fail, the component will still render with empty data
        return (
            <CategoryClientSideComponent
                initialProducts={initialProducts}
                optionsAndOptionVariations={optionsAndOptionVariations}
                categoryId={categoryId}
            />
        );
    } catch (error) {
        console.error("Error fetching products:", error);
        // Render with fallback data instead of error message
        return (
            <CategoryClientSideComponent
                initialProducts={fallbackProducts}
                optionsAndOptionVariations={fallbackOptionsAndVariations}
                categoryId={categoryId}
            />
        );
    }
};

export const dynamic = 'force-dynamic'; // Ensure SSR

export default CategoryPage;
