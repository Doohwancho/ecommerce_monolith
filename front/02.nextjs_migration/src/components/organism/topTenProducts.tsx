"use client"
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/molecule/carousel";
import ProductCard from "./productCard";
import Autoplay from "embla-carousel-autoplay"

interface TopTenRatedProductsProps {
  products: GroupedProduct[]; // Ensure this matches the expected type
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

const TopTenRatedProducts: React.FC<TopTenRatedProductsProps> = ({ products }) => {
  return (
    <div className="py-12">
      <h2 className="flex justify-center text-3xl font-extrabold text-gray-900 sm:text-4xl">
        Trending Now
      </h2>
      <p className="flex justify-center items-center text-center mt-4 text-lg text-gray-500">
        평범한 일상, 특별한 스타일
      </p>
      <Carousel 
        plugins={[
            Autoplay({delay:2000})
        ]}
      >
      {/* <Carousel> */}
      <CarouselContent>
        {products.map((product) => (
          <CarouselItem key={product.productId} className="lg:basis-1/3">
            <ProductCard product={product} /> {/* Pass the product prop */}
          </CarouselItem>
        ))}
      </CarouselContent>
        {/* <CarouselPrevious /> */}
        {/* <CarouselNext /> */}
      </Carousel>
    </div>
  );
};

export default TopTenRatedProducts;
