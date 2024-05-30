import Link from "next/link";
import Image from "next/image";

const ProductCard = () => {
  return (
    <div className="p-4 md:p-6">
      <div className="relative group overflow-hidden rounded-lg">
        <Link className="absolute inset-0 z-10" href="#">
          <span className="sr-only">View</span>
        </Link>
        <Image 
          src="/assets/image/category-product-image-1.webp"
          width={300}
          height={300}
          alt="product image"
        />
        <div className="bg-white p-4 dark:bg-gray-950">
          <h3 className="font-semibold text-lg md:text-xl">
            Stylish Sunglasses
          </h3>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            UV protection
          </p>
          <h4 className="font-semibold text-base md:text-lg">$29.99</h4>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;