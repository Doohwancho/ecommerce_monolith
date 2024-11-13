import Footer from "@/components/organism/footer";
import HomeLayout from "@/components/template/homeLayout";
import Image from 'next/image';
import mainImage from '/public/assets/image/main-image.webp'
import HomeBanner from "@/components/organism/homeBanner";
import TopTenRatedProducts from "@/components/organism/topTenProducts";
import { ProductDTO } from '../../models';

export interface HomeProps {
  products: GroupedProduct[];
}

export interface GroupedProduct {
  productId: number;
  productName: string;
  price: number;
  description: string;
  rating: number;
  ratingCount: number;
  option_variation_id: { [key: number]: number }; 
}

// Fallback data for when the API call fails
const fallbackProducts: GroupedProduct[] = [
  {
    productId: 0,
    productName: "Loading...",
    price: 0,
    description: "Product information is currently unavailable",
    rating: 0,
    ratingCount: 0,
    option_variation_id: {},
  },
];

async function fetchProducts(): Promise<GroupedProduct[]> {
  try {
    const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
    
    if (!BASE_URL) {
      console.error('API URL is not configured');
      return fallbackProducts;
    }

    const endpoint = `/products/highestViews`;
    const fullUrl = BASE_URL + endpoint;

    const response = await fetch(fullUrl, {
      next: { revalidate: 60 }, // Revalidate every 60 seconds for ISR
    });

    if (!response.ok) {
      throw new Error(`API request failed with status ${response.status}`);
    }

    const products: ProductDTO[] = await response.json();

    // Transform the data with error handling
    return products.map((product: ProductDTO) => ({
      productId: product.productId ?? 0,
      productName: product.name ?? 'Untitled Product',
      price: 0,
      description: product.description ?? 'No description available',
      rating: product.rating ?? 0,
      ratingCount: product.ratingCount ?? 0,
      option_variation_id: {},
    }));

  } catch (error) {
    // Log the error for debugging
    console.error('Error fetching products:', error);
    
    // Return fallback data instead of throwing
    return fallbackProducts;
  }
}

export default async function Home() {
  let products: GroupedProduct[] = fallbackProducts;

  try {
    products = await fetchProducts();

    // Validate the returned data
    if (!Array.isArray(products) || products.length === 0) {
      console.warn('No products returned from API, using fallback data');
      products = fallbackProducts;
    }
  } catch (error) {
    // This catch block handles any errors that might occur during data fetching
    console.error('Error in Home component:', error);
    // Use fallback data
    products = fallbackProducts;
  }

  return (
    <>
      <HomeLayout>
        <Image src={mainImage} alt='main image' priority />
        <HomeBanner />
        <TopTenRatedProducts products={products} />
      </HomeLayout>
    </>
  );
}

// Add error boundary for client-side errors
export function generateMetadata() {
  return {
    title: 'Home | Your Store Name',
    description: 'Welcome to our store featuring top-rated products',
  };
}