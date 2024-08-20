import Footer from "@/components/organism/footer";
import HomeLayout from "@/components/template/homeLayout";
import Image from 'next/image';
import mainImage from '/public/assets/image/main-image.webp'
import HomeBanner from "@/components/organism/homeBanner";
import TopTenRatedProducts from "@/components/organism/topTenProducts";
import { ProductDTO } from '../../../nextjs_migration/models/';

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

async function fetchProducts() {
  const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
  const endpoint = `/products/highestRatings`;
  const fullUrl = BASE_URL + endpoint;

  const response = await fetch(fullUrl, {
    next: { revalidate: 10 }, // Revalidate every 10 seconds for ISR
  });
  const products = await response.json();

  return products;
}

export default async function Home() {
  const products = await fetchProducts(); 
  
  const groupedProducts: GroupedProduct[] = products.map((product: ProductDTO) => ({
    productId: product.productId,
    productName: product.name,
    price: 0,
    description: product.description,
    rating: product.rating,
    ratingCount: product.ratingCount,
    option_variation_id: {},
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