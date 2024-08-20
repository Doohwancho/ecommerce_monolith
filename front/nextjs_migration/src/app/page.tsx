import Footer from "@/components/organism/footer";
import HomeLayout from "@/components/template/homeLayout";
import Image from 'next/image';
import mainImage from '/public/assets/image/main-image.webp'
import HomeBanner from "@/components/organism/homeBanner";
import TopTenRatedProducts from "@/components/organism/topTenProducts";
import { ProductDTO } from '../../../nextjs_migration/models/';

export interface HomeProps {
  products: ProductDTO[];
}

export interface GroupedProduct {
  productId: number;
  productName: string;
  price: number;
  description: string;
  rating: number;
  ratingCount: number;
  option_variation_id: { [key: number]: number }; // Change this to an object mapping option variation IDs to product item IDs
}

export async function fetchProducts() {
  const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
  const endpoint = `/products/highestRatings`;
  const fullUrl = BASE_URL + endpoint;

  const response = await fetch(fullUrl);
  const products = await response.json();

  return products;
}

export default async function Home() {
  const products = await fetchProducts(); // Fetch products directly here
  
  const groupedProducts: GroupedProduct[] = products.map((product : ProductDTO) => ({
    productId: product.productId,
    productName: product.name,
    price: 0, // Set a default price or fetch it if available
    description: product.description,
    rating: product.rating,
    ratingCount: product.ratingCount,
    option_variation_id: {}, // Set this as needed
  }));

  return (
    <>
      <HomeLayout>
        <Image src={mainImage} alt='main image' />
        <HomeBanner />
        <TopTenRatedProducts products={groupedProducts} />
      </HomeLayout>
    </>
  );
}
