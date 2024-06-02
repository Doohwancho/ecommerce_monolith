import Link from "next/link";
import Image from "next/image";

const ProductCard = () => {
  return (
        <div className="relative group">
          <Link className="absolute inset-0 z-10" href="#">
            <span className="sr-only">View</span>
          </Link>

          <Image 
            src="/assets/image/category-product-image-1.webp"
            width={300}
            height={300}
            alt="product image"
            />
          <div className="flex-1 py-4">
            <h3 className="font-semibold tracking-tight">
              Stylish Running Shoes
            </h3>
            <small className="text-sm leading-none text-gray-500 dark:text-gray-400">
              Comfortable and Durable
            </small>
            <h4 className="font-semibold">$79.99</h4>
          </div>
        </div>
  );
};

export default ProductCard;