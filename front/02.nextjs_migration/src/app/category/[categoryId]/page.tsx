import { ProductWithOptionsListVer2ResponseDTO, CategoryOptionsOptionVariationsResponseDTO } from '../../../../models';
import CategoryClientSideComponent from './CategoryClientSideComponent';

interface CategoryPageProps {
    params: {
        categoryId: string;
    };
}

const fetchProductsWithOptionsByCategoryId = async (categoryId: string): Promise<ProductWithOptionsListVer2ResponseDTO> => {
    const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
    const endpoint = `/products/category/v2/${categoryId}`;
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
            throw new Error('Network response was not ok');
        }

        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            console.error('Received non-JSON response:', text);
            throw new Error("Received non-JSON response from server");
        }

        return response.json();
    } catch (error) {
        console.error("Fetch error:", error);
        throw error;
    }
};

const fetchOptionsAndOptionVariationsByCategoryId = async (categoryId: string): Promise<CategoryOptionsOptionVariationsResponseDTO> => {
    const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
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
            throw new Error('Network response was not ok');
        }

        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            console.error('Received non-JSON response:', text);
            throw new Error("Received non-JSON response from server");
        }

        return response.json();
    } catch (error) {
        console.error("Fetch error:", error);
        throw error;
    }
};

const CategoryPage = async ({ params }: CategoryPageProps) => {
    const { categoryId } = params;

    try {
        const initialProducts = await fetchProductsWithOptionsByCategoryId(categoryId);
        const optionsAndOptionVariations = await fetchOptionsAndOptionVariationsByCategoryId(categoryId);
        
        return <CategoryClientSideComponent initialProducts={initialProducts} optionsAndOptionVariations={optionsAndOptionVariations} categoryId={categoryId} />;
    } catch (error) {
        console.error("Error fetching products:", error);
        // You might want to render an error component here
        return <div>Error loading products. Please try again later.</div>;
    }
};

export const dynamic = 'force-dynamic'; // Ensure SSR

export default CategoryPage;
