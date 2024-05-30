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



const TopTenRatedProducts = () => {
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
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>

          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
          <CarouselItem className="lg:basis-1/3">
            <ProductCard />
          </CarouselItem>
        </CarouselContent>
        {/* <CarouselPrevious /> */}
        {/* <CarouselNext /> */}
      </Carousel>
    </div>
  );
};

export default TopTenRatedProducts;
